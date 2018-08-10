package com.articulate.sigma.trans;

import com.articulate.sigma.*;
import com.articulate.sigma.trans.SUMOformulaToTPTPformula;

import java.io.*;
import java.util.*;

public class SUMOKBtoTPTPKB {

    public static final boolean filterSimpleOnly = false;

    public KB kb;

    public static boolean debug = false;

    public static String lang = "tff";

    public static HashSet<String> excludedPredicates = new HashSet<>();

    public ArrayList<String> alreadyWrittenTPTPs = new ArrayList<String>();

    /** *************************************************************
     */
    public SUMOKBtoTPTPKB() {

        buildExcludedPredicates();
    }

    /** *************************************************************
     * define a set of predicates which will not be used for inference
     */
    public static HashSet<String> buildExcludedPredicates() {

        excludedPredicates.add("documentation");
        excludedPredicates.add("domain");
        excludedPredicates.add("format");
        excludedPredicates.add("termFormat");
        excludedPredicates.add("externalImage");
        excludedPredicates.add("relatedExternalConcept");
        excludedPredicates.add("relatedInternalConcept");
        excludedPredicates.add("formerName");
        excludedPredicates.add("abbreviation");
        excludedPredicates.add("conventionalShortName");
        excludedPredicates.add("conventionalLongName");

        return excludedPredicates;
    }

    /** *************************************************************
     */
    public String copyFile(String fileName) {

        String outputPath = "";
        FileReader in = null;
        FileWriter out = null;
        try {
            String sanitizedKBName = kb.name.replaceAll("\\W","_");
            File inputFile = new File(fileName);
            File outputFile = File.createTempFile(sanitizedKBName, ".p", null);
            outputPath = outputFile.getCanonicalPath();
            in = new FileReader(inputFile);
            out = new FileWriter(outputFile);
            int c;
            while ((c = in.read()) != -1)
                out.write(c);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            }
            catch (Exception ieo) {
                ieo.printStackTrace();
            }
        }
        return outputPath;
    }

    /** *************************************************************
     */
    public static void addToFile (String fileName, ArrayList<String> axioms, String conjecture) {

        DataOutputStream out = null;
        try {
            boolean append = true;
            FileOutputStream file = new FileOutputStream(fileName, append);
            out = new DataOutputStream(file);
            if (axioms != null) {   // add axioms
                for (String axiom : axioms)
                    out.writeBytes(axiom);
                out.flush();
            }
            if (StringUtil.isNonEmptyString(conjecture)) {  // add conjecture
                out.writeBytes(conjecture);
                out.flush();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                if (out != null) out.close();
            }
            catch (Exception ioe) {
                ioe.printStackTrace();
            }
        }
        return;
    }

    /** ***************************************************************
     * @param relationMap is a Map of String keys and values where
     *                    the key is the renamed relation and the
     *                    value is the original name.
     */
    protected void printVariableArityRelationContent(PrintWriter pr, TreeMap<String,String> relationMap,
                                                     String sanitizedKBName, int axiomIndex, boolean onlyPlainFOL) {

        Iterator<String> it = relationMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = relationMap.get(key);
            ArrayList<Formula> result = kb.ask("arg",1,value);
            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    Formula f = result.get(i);
                    String s = f.theFormula.replace(value,key);
                    if (!onlyPlainFOL) {
                        pr.println(lang + "(kb_" + sanitizedKBName + "_" + axiomIndex++ +
                                ",axiom,(" + SUMOformulaToTPTPformula.tptpParseSUOKIFString(s, false) + ")).");
                    }
                    else {
                    //    pr.println("%FOL fof(kb_" + sanitizedKBName + "_" + axiomIndex++ +
                    //            ",axiom,(" + SUMOformulaToTPTPformula.tptpParseSUOKIFString(s, false) + ")).");
                    }
                }
            }
        }
    }

    /** *************************************************************
     *  Sets reasoner and calls writeTPTPFile() below
     */
    public String writeFile(String fileName,
                                boolean onlyPlainFOL) {

        if (debug) System.out.println("INFO in SUMOKBtoTPTPKB.writeTPTPFile(1): onlyPlainFOL: " + onlyPlainFOL);
        final String reasoner = "EProver";
        return writeFile(fileName,onlyPlainFOL,
                reasoner);
    }

    /** *************************************************************
     *  Sets conjecture and calls writeTPTPFile() below
     */
    public String writeFile(String fileName,
                                boolean onlyPlainFOL,
                                String reasoner) {

        if (debug) System.out.println("INFO in SUMOKBtoTPTPKB.writeTPTPFile(2): onlyPlainFOL: " + onlyPlainFOL);
        final Formula conjecture = null;
        return writeFile(fileName,conjecture,onlyPlainFOL,
                reasoner);
    }

    /** *************************************************************
     *  Sets isQuestion and calls writeTPTPFile() below
     */
    public String writeFile(String fileName,
                                Formula conjecture,
                                boolean onlyPlainFOL,
                                String reasoner) {

        if (debug) System.out.println("INFO in SUMOKBtoTPTPKB.writeTPTPFile(3): onlyPlainFOL: " + onlyPlainFOL);
        final boolean isQuestion = false;
        return writeFile(fileName,conjecture,onlyPlainFOL,
                reasoner,isQuestion);
    }

    /** *************************************************************
     *  Sets pw and calls writeTPTPFile() below
     */
    public String writeFile(String fileName,
                                Formula conjecture,
                                boolean onlyPlainFOL,
                                String reasoner,
                                boolean isQuestion) {

        if (debug) System.out.println("INFO in SUMOKBtoTPTPKB.writeTPTPFile(4): onlyPlainFOL: " + onlyPlainFOL);
        final PrintWriter pw = null;
        return writeFile(fileName,conjecture,onlyPlainFOL,
                reasoner,isQuestion,pw);
    }

    /** *************************************************************
     */
    public class OrderedFormulae extends TreeSet<Formula> {

        public int compare(Object o1, Object o2) {
            Formula f1 = (Formula) o1;
            Formula f2 = (Formula) o2;
            int fileCompare = f1.sourceFile.compareTo(f2.sourceFile);
            if (fileCompare == 0) {
                fileCompare = (Integer.valueOf(f1.startLine))
                        .compareTo(Integer.valueOf(f2.startLine));
                if (fileCompare == 0) {
                    fileCompare = (Long.valueOf(f1.endFilePosition))
                            .compareTo(Long.valueOf(f2.endFilePosition));
                }
            }
            return fileCompare;
        }
    }

    /** *************************************************************
     */
    public void writeHeader(PrintWriter pw, String sanitizedKBName) {

        if (pw == null) {
            pw.println("% Articulate Software");
            pw.println("% www.ontologyportal.org www.articulatesoftware.com");
            pw.println("% This software released under the GNU Public License <http://www.gnu.org/copyleft/gpl.html>.");
            pw.println("% This is a translation to TPTP of KB " + sanitizedKBName);
            pw.println("");
        }
    }

    /** *************************************************************
     *  Write all axioms in the KB to TPTP format.
     *
     * @param fileName - the full pathname of the file to write
     */
    public String writeFile(String fileName, Formula conjecture, boolean onlyPlainFOL,
                            String reasoner, boolean isQuestion, PrintWriter pw) {

        //if (debug && pw != null) pw.println("% INFO in SUMOKBtoTPTPKB.writeTPTPFile(5): onlyPlainFOL: " + onlyPlainFOL);
        String result = null;
        PrintWriter pr = null;
        try {
            File outputFile;
            int axiomIndex = 1;   // a count appended to axiom names to make a unique ID
            TreeMap<String,String> relationMap = new TreeMap<String,String>(); // A Map of variable arity relations keyed by new name
            String sanitizedKBName = kb.name.replaceAll("\\W","_");
            //----If file name is a directory, create filename therein
            if (fileName == null) {
                outputFile = File.createTempFile(sanitizedKBName, ".p", null);
                //----Delete temp file when program exits.
                outputFile.deleteOnExit();
            }
            else
                outputFile = new File(fileName);
            String canonicalPath = outputFile.getCanonicalPath();
            if (pw instanceof PrintWriter)
                pr = pw;
            else
                pr = new PrintWriter(new FileWriter(outputFile));
            writeHeader(pr,sanitizedKBName);

            OrderedFormulae orderedFormulae = new OrderedFormulae();
            orderedFormulae.addAll(kb.formulaMap.values());
            //if (debug) pw.println("% INFO in SUMOKBtoTPTPKB.writeTPTPFile(): added formulas: " + orderedFormulae.size());
            List<String> tptpFormulas = null;
            File sf = null;
            int counter = 0;
            for (Formula f : orderedFormulae) {
                pr.println("% f: " + f.format("",""," "));
                pr.println("% from file " + f.sourceFile + " at line " + f.startLine);
                counter++;
                if (counter == 100) { System.out.print("."); counter = 0; }
                FormulaPreprocessor fp = new FormulaPreprocessor();
                fp.debug = true;
                List<Formula> processed = fp.preProcess(f,false,kb);
                //if (debug) pw.println("% INFO in SUMOKBtoTPTPKB.writeTPTPFile(): processed formulas: " + processed);
                if (!processed.isEmpty()) {
                    ArrayList<Formula> withRelnRenames = new ArrayList<Formula>();
                    for (Formula f2 : processed)
                        withRelnRenames.add(f2.renameVariableArityRelations(kb,relationMap));
                    for (Formula f3 : withRelnRenames) {
                        if (lang.equals("tptp")) {
                            SUMOformulaToTPTPformula stptp = new SUMOformulaToTPTPformula();
                            stptp._f = f3;
                            stptp.tptpParse(f3, false, kb, withRelnRenames);
                        }
                        else if (lang.equals("tff")) {
                            SUMOtoTFAform stptp = new SUMOtoTFAform();
                            f3.theTptpFormulas = new ArrayList<>();
                            if (withRelnRenames != null) {
                                String tfaForm = stptp.process(f3.theFormula);
                                if (tfaForm != null)
                                    f3.theTptpFormulas.add(tfaForm);
                            }
                        }
                        if (f != null && f3 != null)
                            f.theTptpFormulas.addAll(f3.theTptpFormulas);
                        else
                            System.out.println("Error in writeFile(): f or f3 is null");
                    }
                }
                //if (debug) pw.println("% INFO in SUMOKBtoTPTPKB.writeTPTPFile(): formula: " + f.format("","",""));
                //if (debug) pw.println("% INFO in SUMOKBtoTPTPKB.writeTPTPFile(): tptp formulas: " + f.theTptpFormulas);

                for (String theTPTPFormula : f.theTptpFormulas) {
                    if (!filterAxiom(f,theTPTPFormula)) {
                        pr.print(lang + "(kb_" + sanitizedKBName + "_" + axiomIndex++);
                        pr.println(",axiom,(" + theTPTPFormula + ")).");
                        alreadyWrittenTPTPs.add(theTPTPFormula);
                    }
                }
                pr.flush();
            }
            System.out.println();
            printVariableArityRelationContent(pr,relationMap,sanitizedKBName,axiomIndex,onlyPlainFOL);
            if (conjecture != null) {  //----Print conjecture if one has been supplied
                // conjecture.getTheTptpFormulas() should return a
                // List containing only one String, so the iteration
                // below is probably unnecessary
                String type = "conjecture";
                if (isQuestion) type = "question";
                for (String theTPTPFormula : conjecture.theTptpFormulas)
                    pr.println(lang + "(prove_from_" + sanitizedKBName + "," + type + ",(" + theTPTPFormula + ")).");
            }
            result = canonicalPath;
        }
        catch (Exception ex) {
            System.out.println("Error in SUMOKBtoTPTPKB.writeTPTPfile(): " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            try {
                //kb.kbCache.clearSortalTypeCache();
                if (pr != null) pr.close();
            }
            catch (Exception ioe) {
                ioe.printStackTrace();
            }
        }
        return result;
    }

    /** *************************************************************
     * @return true if the given formula is simple clause,
     *   and contains one of the excluded predicates;
     * otherwise return false;
     */
    public boolean filterExcludePredicates(Formula formula) {

        boolean pass = false;
        if (formula.isSimpleClause(kb))
            pass = excludedPredicates.contains(formula.getArgument(0));
        return pass;
    }

    /** *************************************************************
     */
    public boolean filterAxiom(Formula form, String tptp) {

        //----Don't output ""ed ''ed and numbers
        if (tptp.matches(".*'[a-z][a-zA-Z0-9_]*\\(.*") ||
                tptp.indexOf("'") > -1
                || tptp.indexOf('"') >= 0) {
            //pr.print("%FOL ");
            System.out.println("% quoted thing or number");
            return true;
        }

        if (filterExcludePredicates(form) == false) {
            if (!alreadyWrittenTPTPs.contains(form)) {
                return false;
            }
        }
        else {
            System.out.println("% filtered predicate");
            return true;
        }
        return false;
    }

    /** *************************************************************
     */
    public static void main(String[] args) {

        //debug = true;
        KBmanager.getMgr().initializeOnce();
                /* not needed since initialization will create the tptp file
        SUMOKBtoTPTPKB skbtptpkb = new SUMOKBtoTPTPKB();

        skbtptpkb.kb = KBmanager.getMgr().getKB(KBmanager.getMgr().getPref("sumokbname"));
        String filename = KBmanager.getMgr().getPref("kbDir") + File.separator + "SUMO.tptp";
        String fileWritten = skbtptpkb.writeTPTPFile(filename, null, true, "none");
        if (StringUtil.isNonEmptyString(fileWritten))
            System.out.println("File written: " + fileWritten);
        else
            System.out.println("Could not write " + filename);
        return;
        */
    }
}
