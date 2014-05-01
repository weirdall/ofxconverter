from config import Config
from input.bank import Bank
from output.ofx import Ofx
from model.bankStatement import BankStatement
from model.transaction import Transaction
from csvReader import CsvReader

from PIL import ImageTk

from tkinter import Tk, Toplevel, Frame, Menu, filedialog, constants, ttk, Text
from tkinter import Label, Checkbutton, IntVar, StringVar, Scrollbar, Canvas, Message
from tkinter import PhotoImage, Entry, Button
import tkinter._fix # needed for cx_freeze
import csv
import collections

class OfxConverter(Frame):
  
    def __init__(self, parent):
        Frame.__init__(self, parent, background="white")   
         
        self.parent = parent
        self.guiMap = []
        self.debetValue = StringVar()
        self.creditValue = StringVar()
        self.row = 0
        self.frame = Frame()

        self.UNUSED = "Unused"
        
        self.initUI()

    def writeLog(self,*msgs):
        self.log['state'] = 'normal'
        if self.log.index('end-1c')!='1.0':
            self.log.insert('end', '\n')
        for msg in msgs:
            self.log.insert('end', msg)
##        self.log['state'] = 'disabled'

    def help(self):
        t = Toplevel()
        t.wm_title("About")
        t.transient()

        photo = ImageTk.PhotoImage(file="chameleon.png")
        
##        photo = PhotoImage(file="chameleon.gif")
        w = Label(t, image=photo)
        w.photo = photo
        w.pack(fill=constants.BOTH,side=constants.LEFT,expand=True)
        
        l = Label(t, text="OFX Converter\n\n"+
                  "Convert files in csv format to the\nofx format " +
                  "which is used by a lot\nof accounting programs " +
                  "like gnucash\n\n Written in Python 3\nby Floris Groenendijk"
                  )
        l.pack(side=constants.TOP, fill="both", expand=False, padx=20, pady=20)

    def onComboboxChanged(self, event):
        if event.widget.get() == 'debet':
            # check which checkbox in which column was changed
            for i in range(len(self.comboBoxes)-1):
                if self.comboBoxes[i] == event.widget:
                    break
                
            values = []

            # retrieve the values of the labels in that column
            for j in range(len(self.labels)-1):
                if self.labels[j][i]['text'] not in values:
                    values.append(self.labels[j][i]['text'])
            
            self.debetCombo['values'] = values
            self.debetCombo.current( 0 )

            if len( values ) > 1:
                self.creditCombo['values'] = values
                self.creditCombo.current( 1 )

    def onFrameConfigure(self, event):
        # Reset the scroll region to encompass the inner frame
        self.canvas.configure(scrollregion=self.canvas.bbox("all"))

    def initUI(self):
      
        self.parent.title("OFX Converter")

        menubar = Menu(self.parent)
        self.parent.config(menu=menubar)
        
        fileMenu = Menu(menubar)
        fileMenu.add_command(label="Open", command=self.openFile, accelerator="Ctrl-o")
        fileMenu.add_command(label="Exit", command=self.onExit, accelerator="Ctrl-e", underline=1 )
        menubar.add_cascade(label="File", menu=fileMenu)
        helpMenu = Menu(menubar)
        helpMenu.add_command(label="About", command=self.help, accelerator="Ctrl-i")
        menubar.add_cascade(label="Info", menu=helpMenu)

        notebook = ttk.Notebook( self.parent )
        tabFilesMain = Frame( notebook )

        self.tabFiles = Frame( tabFilesMain )
        self.tabFiles.pack(fill=constants.BOTH)

        button = ttk.Button( tabFilesMain, command=self.parseFile, text="Process" )
        button.pack(side="bottom", fill=constants.X, padx=5, pady=5)
        
        tabLogging = Canvas( notebook )
        tabLogging.grid_rowconfigure(0, weight=1)
        tabLogging.grid_columnconfigure(0, weight=1)

        tabCustomCsv = Frame( notebook )
        
        self.canvas = Canvas( tabCustomCsv, highlightthickness=0 )

        scrollbar=Scrollbar( tabCustomCsv,orient="horizontal",command=self.canvas.xview)
        self.canvas.configure(xscrollcommand=scrollbar.set)

        self.canvas.pack(fill=constants.BOTH,expand=True)
        scrollbar.pack(side="bottom", fill=constants.X)

        Label( tabCustomCsv, text="Values to determine whether the debet field concerns a debet or credit transaction" ).pack(anchor=constants.W)

        Label( tabCustomCsv, text="debet", width=6 ).pack(side=constants.LEFT)
        self.debetCombo = ttk.Combobox( tabCustomCsv,width=10,text="debet" )
        self.debetCombo.pack(side=constants.LEFT)

        Label( tabCustomCsv, text="credit", width=6 ).pack(side=constants.LEFT)
        self.creditCombo = ttk.Combobox(tabCustomCsv,width=10,text="credit")
        self.creditCombo.pack(side=constants.LEFT)

        Button( tabCustomCsv, text="save configuration", command=self.saveConfig ).pack(side=constants.RIGHT )

        self.log = Text(tabLogging, wrap='word')
        self.log.grid(row=0,column=0,sticky='news')

        hScroll = Scrollbar(tabLogging, orient=constants.HORIZONTAL, command=self.log.xview)
        hScroll.grid(row=1, column=0, sticky='we')
        vScroll = Scrollbar(tabLogging, orient=constants.VERTICAL, command=self.log.yview)
        vScroll.grid(row=0, column=1, sticky='ns')
        self.log.configure(xscrollcommand=hScroll.set, yscrollcommand=vScroll.set)

        notebook.add( tabFilesMain, text="Files to process" )
        notebook.add( tabCustomCsv, text="Custom csv" )
        notebook.add( tabLogging, text="Logging" )

        notebook.pack(fill=constants.BOTH,expand=1,anchor=constants.N)

        self.tabFiles.grid_columnconfigure( 0, weight=1 ) 

    def addFile(self,filename,ibans):
        if filename != "" and len(ibans) > 0:
            Label(self.tabFiles, text=filename,
                    borderwidth=3).grid(row=self.row,column=0,sticky=constants.W,padx=1)
            
            ibanList = []
            for iban in ibans:
                ibanList.append( iban[:8] )
            combo = ttk.Combobox(self.tabFiles,values=ibanList)
            combo.current(0)
            if len(ibanList) == 1:
                combo.configure(state=constants.DISABLED)
            combo.grid(row=self.row,column=1,sticky=constants.E,padx=1)
            
            state = IntVar()
            c = Checkbutton(self.tabFiles,variable=state)
            c.grid(row=self.row,column=2)
            self.row += 1
            ttk.Separator(self.tabFiles).grid(row=self.row, sticky="ew", columnspan=3 )
            self.row += 1
            self.guiMap.append( [ filename, ibans, combo, c, state ] )

    def addFileToCustomTab(self,filename):
        if filename != "":

            if self.frame:
                self.frame.pack_forget()
                self.frame.destroy()
            self.frame = Frame( self.canvas )
            self.canvas.create_window((0,0),window=self.frame,anchor='nw')
            self.frame.bind("<Configure>", self.onFrameConfigure)
             
            file = csv.reader( open(filename) )
            lines = 1

            transaction = Transaction()
            fields = transaction.fields
            fields.insert(0,"main account")
            fields.insert(0,self.UNUSED)

            self.comboBoxes = []
            self.labels = collections.defaultdict(list)
            
            for row in file:
                column = 0
                for field in row:
                    if lines == 1:
                        combo = ttk.Combobox(self.frame,values=transaction.fields,state="readonly")
                        combo.current(0)
                        combo.grid(row=0,column=column,sticky=constants.W)
                        self.comboBoxes.append( combo )
                        combo.bind('<<ComboboxSelected>>', self.onComboboxChanged)
                        nextColumn = column + 1
                        ttk.Separator(self.frame,orient=constants.VERTICAL).grid(row=0, column=nextColumn, sticky="ns")
                            
                    label = Label(self.frame,text=field,borderwidth=3)
                    label.grid(row=lines,column=column,sticky=constants.W,padx=1)
                    self.labels[lines-1].append( label )
                    column = column + 1
                    ttk.Separator(self.frame,orient=constants.VERTICAL).grid(row=lines, column=column, sticky="ns")
                    column = column + 1

                lines = lines + 1
                if lines > 2:
                    break

    def saveConfig(self):
        fields = []
        memos = []
        for i in range( len(self.comboBoxes) - 1 ):
            key = self.comboBoxes[i].get()
            if key == self.UNUSED:
                continue
            elif key == 'debet':
                fields.append( [ key, ' '.join( [ str(i), self.creditCombo.get(), self.debetCombo.get() ] )] )
            elif key == 'memo':
                memos.append( str(i) )
            elif key == 'main account':
                fields.append( [ key, self.labels[0][i]['text'] ] )
            else:
                fields.append( [ key, i ] )

        if len(memos) > 0:
            fields.append( [ 'memo', ' '.join( memos ) ] )

        print( fields )

    def openFile(self):
        filename = filedialog.askopenfilename(parent=self.parent,
                                                filetypes=[('Csv files','.csv'),
                                                           ('All Files','.*')],
                                                title='Select the csv')
        if filename != "":
            self.writeLog( 'File added: ', filename )

            bank = Bank()

            ibans = bank.searchMainIban( filename )

            # If mainIban contains more than one iban,
            # let the user select which one is the main iban
            # Otherwise we know the bank this csv belongs to

            if len( ibans ) > 1:
                self.writeLog( 'there\'s too many ibans, please select one from the list' )
            elif len( ibans ) == 0:
                self.writeLog( 'No ibans found, is the file correct?' )
                ## adding file to custom csv tab
                
            else:
                self.writeLog( 'Found iban: ', ibans[0][:8] )
                ibanType = ibans[0][:8]

            self.addFile(filename,ibans)
            self.addFileToCustomTab( filename )


    def parseFile(self):

        for guiLine in self.guiMap:
            ( filename, ibans, combo, checkButton, state ) = guiLine

            if state.get():
                ibanType = combo.get()
                
                config = Config()
                fields = config.getCurrentBank( ibanType )

                bankStatement = BankStatement()

                bankStatement.account = ibans[0]

                csvReader = CsvReader(fields)
            
                csvReader.readFile( filename, bankStatement )

                ofx = Ofx()

                ofx.createXmlFile( filename, bankStatement )
                checkButton.configure(state=constants.DISABLED)

    def onExit(self):
        quit()

if __name__ == '__main__':

    root = Tk()
    root.option_add('*tearOff', False)

    root.geometry("700x400+300+300")

    app = OfxConverter(root)
    root.mainloop()
        
