package com.company;

// Java ParserAppram to create a text ParserApp using java
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.plaf.metal.*;
import javax.swing.text.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

class ParserApp extends JFrame implements ActionListener {
    public static int count = 0;
    private static ArrayList<String> rules = new ArrayList<String>();
    private static ArrayList<Integer> lasttermspecialRule = new ArrayList<Integer>();
    private static ArrayList<Integer> firsttermspecialRule = new ArrayList<Integer>();
    private static ArrayList<String> parts = new ArrayList<String>();
    private static ArrayList<String> rightparts = new ArrayList<String>();//this array contains all the right parts
    private static ArrayList<String> terminals = new ArrayList<String>();
    private static ArrayList<Integer> indexes = new ArrayList<Integer>();
    private static ArrayList<String> inputs = new ArrayList<String>();
    private static ArrayList<String>[] parse = new ArrayList[3];
    private static ArrayList<String>[] firstterms;
    public static ArrayList<String>[] lastterms ;
    public static String [][]parseTable;
    public static String[] array=new String[4];
    public static int counter=0;





    // Text component
    JTextArea t;
    // Frame
    JFrame f;
    // Constructor
    ParserApp()
    {
        // Create a frame
        f = new JFrame("ParserApp");
        try {
            // Set metal look and feel
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            // Set theme to ocean
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        }
        catch (Exception e) {
        }
        // Text component
        t = new JTextArea();
        // Create a menubar
        JMenuBar mb = new JMenuBar();
        // Create amenu for menu
        JMenu m1 = new JMenu("File");
        // Create menu items
        JMenuItem mi1 = new JMenuItem("Save");
        JMenuItem mi2 = new JMenuItem("Open");

        // Add action listener
        mi1.addActionListener(this);
        mi2.addActionListener(this);

        m1.add(mi1);
        m1.add(mi2);

        JMenuItem mc = new JMenuItem("close");

        mc.addActionListener(this);

        mb.add(m1);
        mb.add(mc);

        f.setJMenuBar(mb);
        f.add(t);
        f.setSize(500, 500);
        f.show();
    }
    // If a button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();

        if (s.equals("Save")) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

            // Invoke the showsSaveDialog function to show the save dialog
            int r = j.showSaveDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {

                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());

                try {
                    // Create a file writer
                    FileWriter wr = new FileWriter(fi, false);

                    // Create buffered writer to write
                    BufferedWriter w = new BufferedWriter(wr);

                    // Write
                    w.write(t.getText());

                    w.flush();
                    w.close();
                }
                catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(f, "the user cancelled the operation");
        }
        else if (s.equals("Open")) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

            t.setText("");

            // Invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);

            // If the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());

                try {
                    //this code is for taking inputs from file
                    String input="";
                    try{

                        Scanner sc = new Scanner(fi);
                        input = sc.nextLine();
                    }

                    catch(FileNotFoundException ee)
                    {
                        JOptionPane.showMessageDialog(null,"File not found");
                        ee.printStackTrace();
                    }


                    //this code is for taking grammer from file/

                    try
                    {
                        //the file to be opened for reading
                        FileInputStream fis=new FileInputStream("D:\\6th Semester Self\\compiler\\Romangrmar.txt");
                        Scanner grm=new Scanner(fis);    //file to be scanned
                        //returns true if there is another line to read
                        while(grm.hasNextLine())
                        {
                            array[counter]=grm.nextLine(); //returns the line that was skipped

                            rules.add(array[counter]);
                            counter++;
                            if(counter>3)
                                break;


                        }
                        grm.close();     //closes the scanner
                    }
                    catch(IOException eee)
                    {
                        eee.printStackTrace();
                    }





                    int n = rules.size();
                    StringBuilder sb= new StringBuilder();
                    for(int i=0; i<n; i++) sb.append(rules.get(i));
                    String grammar = sb.toString();
                    for(int i=0; i<grammar.length(); i++){
                        if(lowercase(grammar.charAt(i)) && nonterminal(grammar.charAt(i)))
                            terminals.add(Character.toString(grammar.charAt(i)));
                    }
                    Set<String> set = new HashSet<>(terminals);
                    terminals.clear();
                    terminals.addAll(set);
                    parseTable = new String[terminals.size()+1][terminals.size()+1];

                    for(int i=0; i<n; i++){
                        rightparts.addAll(partFinder(rules.get(i)));
                    }
                    int size = rightparts.size();

                    for(int i=0; i<size; i++){
                        String str = rightparts.get(i);
                        char []strchar = str.toCharArray();
                        for(int z=0; z<str.length(); z++){
                            if(uppercase(str.charAt(z))){
                                strchar[z]='N';
                            }
                        }
                        str = String.copyValueOf(strchar);
                        rightparts.set(i, str);//creating the elements

                    }
                    set = new HashSet<>(rightparts);//eliminating repetitive elements
                    rightparts.clear();
                    rightparts.addAll(set);

                    for(int i=0; i<terminals.size()+1; i++){ //initialization
                        for(int f=0; f<terminals.size()+1; f++){
                            parseTable[i][f]= "e";
                        }
                    }

                    int k=1;
                    for(int i=0; i<terminals.size();i++){
                        parseTable[k][0] = terminals.get(i);
                        parseTable[0][k] = terminals.get(i);
                        k++;
                    }
                    parseTable[0][0]=" ";





                    lastterms = new ArrayList[n];//keeping lastterms for each variable
                    firstterms = new ArrayList[n];//keeping firstterms for each variable
                    //############ initializing
                    for (int i = 0; i < n; i++) {
                        lastterms[i] = new ArrayList<String>();
                    }
                    for (int i = 0; i < n; i++) {
                        firstterms[i] = new ArrayList<String>();
                    }
                    for (int i = 0; i < 3; i++) {
                        parse[i] = new ArrayList<String>();
                    }



                    count =0; //clear the global variable for lastterms
                    for (int i = 0; i < n; i++) { //call the function for each rule
                        lasttermFinder(rules.get(i));
                    }

                    for (int i = 0; i < n; i++) //call the function for the number of variables for assurance
                        completeLastterm(lasttermspecialRule);




                    count =0;//clear the global variable for firstterms
                    for (int i = 0; i < n; i++) { //call the function for each rule
                        firsttermFinder(rules.get(i));
                    }

                    for (int i = 0; i < n; i++) //call the function for the number of variables for assurance
                        completeFirstterm(firsttermspecialRule);

                    parseTableGenerator(rules);//filling parse table

                    String PriOpRelationPreTable = "Name: Sagar Ali(023-18-0028) Muhammad Farhan Qureshi(023-18-0034) Muhammad Ibrahim(023-18-0010) \nPurpose Of Development: Semester Project for Compiler Construction.\n  Submitted To: Ma'am Faryal Shamsi.\n\n\n\n";

                    PriOpRelationPreTable += "****************************************************\n"+"Printing Operater Relationship Preedence Table\n"
                            + "****************************************************\n\n";

                    for(int i=0; i<terminals.size()+1; i++){//printing parse table
                        for(int h=0; h<terminals.size()+1; h++){
                            PriOpRelationPreTable +=  parseTable[i][h]+"   ";
                        }

                        PriOpRelationPreTable += "\n";
                    }

                    parse[0].add("$");
                    parse[1].add(input);
                    parser(input);


                    PriOpRelationPreTable += "\n\n\n****************************************************\n"+
                            "Printing Parse Table Results\n"+
                            "****************************************************\n\n";

                    //############## printing parse results

                    PriOpRelationPreTable += "Stack\t\t"+"Input\t\t"+"Action \n"+"--------------------------------------------------\n";
                    String RestultTAble = "";
                    for (int i = 0; i <parse[0].size()-1; i++) { //printing parse
                        RestultTAble +=  parse[0].get(i)+"\t\t"+ parse[1].get(i)+"\t\t"+ parse[2].get(i) + "\n";
                    }
                    //My Code End Here
                    PriOpRelationPreTable += RestultTAble;

                    // Set the text
                    t.setText(PriOpRelationPreTable);
                }
                catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(f, "the user cancelled the operation");
        }
        else if (s.equals("close")) {
            f.setVisible(false);
        }
    }


    //mycODE iS Here.................................
    /**Below are the lists of functions that were used in the ParserAppram**/


    //This function will find the rule based on its first character
    public static int ruleFinder(char a){
        for(int i=0; i<rules.size(); i++){
            if(rules.get(i).charAt(0)==a)
                return i;
        }
        return 0;
    }

    //This function will insert an item in the parse table
    public static void insertParseTable(String s1, String s2, String str){
        int row = 0 ,column = 0;
        for(int i=0; i<terminals.size()+1; i++){
            if(parseTable[i][0].equals(s1)) row=i;
            if(parseTable[0][i].equals(s2)) column=i;
        }
        parseTable[row][column] = str;
    }

    //This function will help eliminating nonterminals
    public static boolean nonterminal(char a){
        if(a==';' || a==':' ||a=='-' ||a=='|' ||a==' ') return false;
        return true;
    }

    //This function will recognize a lowercase which is a terminal
    public static boolean lowercase(char a){
        if(a<65 || a>90) return true;
        else return false;
    }

    //This function will recognize an uppercase which is a variable
    public static boolean uppercase(char a){
        if(a>=65 && a<=90) return true;
        else return false;
    }

    //This function will divide the right side of the given rules
    public static ArrayList<String> partFinder(String rule){
        parts.clear();
        int pos = 0;
        for(int i=0; i<rule.length(); i++){
            if(rule.charAt(i)=='-' || rule.charAt(i)=='|' ||rule.charAt(i)==';'){
                parts.add(rule.substring(pos+1,i).trim());
                pos = i;
            }
        }
        parts.remove(0);//first element is not needed

        return parts;
    }

    //This function will find the initial firstterms
    public static void firsttermFinder(String rule){

        partFinder(rule);
        for(int i=0; i<parts.size();i++){
            if(parts.get(i).length()==1){
                if(lowercase(parts.get(i).charAt(0)))
                    firstterms[count].add(parts.get(i));
                else if(uppercase(parts.get(i).charAt(0)))
                    firsttermspecialRule.add(count);//we deal with this later
            }
            else{
                String str = parts.get(i);
                if(lowercase(str.charAt(0))) {
                    firstterms[count].add(Character.toString(str.charAt(0)));
                } else if(uppercase(str.charAt(0))){
                    if(lowercase(str.charAt(1))) {
                        firstterms[count].add(Character.toString(str.charAt(1)));
                    }
                }

            }
        }
        count ++;
    }

    //This function will complete the firstterms
    public static void completeFirstterm(ArrayList specialRule){//needs modification
        for(int i=0; i<specialRule.size();i++){
            int num = (int)specialRule.get(i);
            ArrayList <String>parts = partFinder(rules.get((int)specialRule.get(i)));
            for (int j=0; j<parts.size();j++){
                if(parts.get(j).length()==1 && uppercase(parts.get(j).charAt(0))){
                    char key = parts.get(j).charAt(0);//the letter of the special rule
                    for (int k=0; k<rules.size();k++){//traverse over the rules for match
                        if(rules.get(k).charAt(0)==key){//assumption: no space at the beginning of the rule
                            /*k is the rule which its firstterms must be added
                             * to the current variable (num)*/
                            for(int m=0; m<firstterms[k].size(); m++){
                                if(!firstterms[num].contains(firstterms[k].get(m)))//if it is not already in the list
                                    firstterms[num].add(firstterms[k].get(m));
                            }

                        }
                    }
                }
            }
        }
    }

    //This function will find the initial lastterms
    public static void lasttermFinder(String rule){

        partFinder(rule);
        for(int i=0; i<parts.size();i++){
            if(parts.get(i).length()==1){
                if(lowercase(parts.get(i).charAt(0)))
                    lastterms[count].add(parts.get(i));
                else if(uppercase(parts.get(i).charAt(0)))
                    lasttermspecialRule.add(count);//we deal with this later
            }
            else{
                String str = parts.get(i);
                if(lowercase(str.charAt(str.length()-1))) {
                    lastterms[count].add(Character.toString(str.charAt(str.length()-1)));
                } else if(uppercase(str.charAt(str.length()-1))){
                    if(lowercase(str.charAt(str.length()-2))) {
                        lastterms[count].add(Character.toString(str.charAt(str.length()-2)));
                    }
                }

            }
        }
        count ++;
    }

    //This function will complete the lastterms
    public static void completeLastterm(ArrayList specialRule){//needs modification
        for(int i=0; i<specialRule.size();i++){
            int num = (int)specialRule.get(i);
            ArrayList <String>parts = partFinder(rules.get((int)specialRule.get(i)));
            for (int j=0; j<parts.size();j++){
                if(parts.get(j).length()==1 && uppercase(parts.get(j).charAt(0))){
                    char key = parts.get(j).charAt(0);//the letter of the special rule
                    for (int k=0; k<rules.size();k++){//traverse over the rules for match
                        if(rules.get(k).charAt(0)==key){//assumption: no space at the beginning of the rule
                            /*k is the rule which its lastterms must be added
                             * to the current variable (num)*/
                            for(int m=0; m<lastterms[k].size(); m++){
                                if(!lastterms[num].contains(lastterms[k].get(m)))//if it is not already in the list
                                    lastterms[num].add(lastterms[k].get(m));
                            }

                        }
                    }
                }
            }
        }
    }

    //This function will fill the parse table with appropriate contents
    public static void parseTableGenerator(ArrayList<String> grammar){
        for(int i=0; i<grammar.size(); i++){
            ArrayList <String>parts = partFinder(grammar.get(i));
            for(int j=0; j<parts.size(); j++){
                String part = parts.get(j);
                if(part.length()>1){
                    for(int k=0; k<part.length()-1; k++){
                        String substr = part.substring(k, k+2);
                        if(lowercase(substr.charAt(0)) && lowercase(substr.charAt(1))){
                            insertParseTable(Character.toString(substr.charAt(0)), Character.toString(substr.charAt(1)), "=");
                        }else if(lowercase(substr.charAt(0)) && uppercase(substr.charAt(1))){
                            int var = ruleFinder(substr.charAt(1));
                            for(int m=0; m<firstterms[var].size(); m++){
                                insertParseTable(Character.toString(substr.charAt(0)), firstterms[var].get(m),"<");
                            }
                        }
                        else if(uppercase(substr.charAt(0)) && lowercase(substr.charAt(1))){
                            int var = ruleFinder(substr.charAt(0));
                            for(int m=0; m<lastterms[var].size(); m++){
                                insertParseTable(lastterms[var].get(m), Character.toString(substr.charAt(1)), ">");
                            }
                        }
                        if(part.length() >= 3){
                            for(int n=0; n<part.length()-2; n++){
                                String sub = part.substring(n,n+3);
                                if(lowercase(sub.charAt(0)) && uppercase(sub.charAt(1)) && lowercase(sub.charAt(2))){
                                    insertParseTable(Character.toString(sub.charAt(0)),Character.toString(sub.charAt(2)), "=");
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    //This function will show the proper comment
    public static void SyntaxError(){
        JOptionPane.showMessageDialog(null,"The parser encountered a problem while parsing the input string.");
    }


    //This function will get the result from the parse table
    public static String fetch(char a, char b){
        int row = 0 ,column = 0;
        String s1 = Character.toString(a);
        String s2 = Character.toString(b);
        for(int i=0; i<terminals.size()+1; i++){
            if(parseTable[i][0].equals(s1)) row=i;
            if(parseTable[0][i].equals(s2)) column=i;
        }
        return parseTable[row][column];
    }

    //This function will correct the handle
    public static String correct(String handle){
        StringBuilder sb = new StringBuilder();
        sb.append(handle);
        sb.deleteCharAt(handle.indexOf('<'));
        String str=sb.toString();
        return str;
    }

    //This function will parse the input string
    public static void parser(String input){
        StringBuilder sb = new StringBuilder();
        String handle = null;
        for(int i=0; i<200; i++){
            if(parse[0].get(i).equals("$N") && parse[1].get(i).equals("$")){
                parse[0].add("$N");
                parse[1].add("$");
                parse[2].add("accept");

                return;
            }
            else{

                String yardstick = null;
                if(parse[0].get(i).charAt(parse[0].get(i).length()-1)!='N')
                    yardstick = fetch(parse[0].get(i).charAt(parse[0].get(i).length()-1),parse[1].get(i).charAt(0));//passing arguments
                if(parse[0].get(i).charAt(parse[0].get(i).length()-1)=='N')
                    yardstick = fetch(parse[0].get(i).charAt(parse[0].get(i).length()-2),parse[1].get(i).charAt(0));
                if(yardstick.equals("e")){//the symbol table entry is empty
                    SyntaxError();
                    return;
                }
                else if(yardstick.equals("=")){
                    sb = new StringBuilder();
                    sb.append(parse[0].get(i));
                    sb.append(parse[1].get(i).charAt(0));
                    String str = sb.toString();
                    parse[0].add(str);
                    sb = new StringBuilder();
                    str = parse[1].get(i);
                    sb.append(str);
                    sb.deleteCharAt(0);
                    str = sb.toString();
                    parse[1].add(str);
                    parse[2].add("shift");
                }
                else if(yardstick.equals("<")){
                    sb = new StringBuilder();
                    sb.append(parse[0].get(i));
                    sb.append("<");
                    sb.append(parse[1].get(i).charAt(0));
                    String str = sb.toString();
                    parse[0].add(str);
                    sb = new StringBuilder();
                    str = parse[1].get(i);
                    sb.append(str);
                    sb.deleteCharAt(0);
                    str = sb.toString();
                    parse[1].add(str);
                    parse[2].add("shift");
                }
                else if(yardstick.equals(">")){
                    sb = new StringBuilder();
                    int index =0;
                    String str= parse[0].get(i);
                    for(int j = str.length()-1; j>=0; j--){
                        if(str.charAt(j) == '<'){
                            index=j;
                            break;
                        }

                    }
                    if(str.charAt(index-1)!='N'){
                        handle = str.substring(index,str.length());
                    }
                    else{
                        handle = str.substring(index-1,str.length());
                    }
                    String result= correct(handle);
                    if(!rightparts.contains(result)){//if the handle does not match with the right side of any rules
                        parse[0].add(parse[0].get(i));
                        parse[1].add(parse[1].get(i));
                        parse[2].add("syntax error");
                        SyntaxError();
                        return;
                    }
                    if(str.charAt(index-1) == 'N') index--;
                    sb.append(str);
                    sb.delete(index, str.length());
                    sb.append("N");
                    str = sb.toString();
                    parse[0].add(str);
                    parse[1].add(parse[1].get(i));
                    parse[2].add("reduce");
                }
            }
        }
    }

}

