package folioxml.lucene;

import folioxml.core.TokenUtils;
import folioxml.lucene.analysis.folio.FolioEnuAnalyzer;
import folioxml.lucene.analysis.folio.FolioEnuPhraseAnalyzer;
import org.apache.lucene.analysis.Analyzer;

public class IndexFieldOpts {
    /*
     * Stop word removal will be used on the field. Not implemented.
     */
    public boolean useStopWords = false;
    //FP, Fast phrase indexing is the lucene default (token vectors).
    /*
	 * Not implemented
	 * DT	Used only with Date fields. Controls how dates specified with a 2-number abbreviation (95, 96, 97, etc.) are handled. 
	 * If this option is used, dates less than or equal to 49 are assumed to be after the year 2000; dates greater than or equal 
	 * to 50 are assumed to be before the year 2000 (such as 1995). If this option is not used, all dates are assumed to be in the 1900s.	
	 */
    public boolean y2kFix = false;

    /*
     * Not implemented.
     * PR	Proximity. All terms in a field primitive must be found within a single application of the field to register a hit. Most often used with TE and TF.
     */
    public boolean writeProximityMarkersPerApplication = false;

    /*
     *
     * If false, any tokens contained by this field will be excluded from indexing in all fields, including the default field. This won't break other fields apart, just delete text from them.
     * Folio Field indexing option 'NO' has this behavior.
     *
     * Verified this via Folio Views experimentation.
     *
     * This can also be false if TF or PF are used by themselves, which results in the exact same behavior (tested).
     *
     */
    public boolean allowOthersToIndex = true;

    /*
     * If true, when a field begins in the exact spot it ended, the breakage will be ignored and it will be considered a single application.
     * Not good for compatibility.
     */
    public boolean mergeTouchingApplications = false;

    public Analyzer fieldAnalyzer = null;

    public IndexFieldOpts(Analyzer a) {
        this.fieldAnalyzer = a;
    }

    public IndexFieldOpts(String[] fieldIndexingFlags) {
        boolean allowOthers = false;

        //If there isn't anything specified, default to allowing others to index.
        if (fieldIndexingFlags.length == 0) allowOthers = true;

        for (String s : fieldIndexingFlags) {
            //TF|PF|TE|NO|PR|DT|FP|SW
            if (TokenUtils.fastMatches("TF", s)) this.fieldAnalyzer = new FolioEnuAnalyzer();
            if (TokenUtils.fastMatches("PF", s)) this.fieldAnalyzer = new FolioEnuPhraseAnalyzer();
            if (TokenUtils.fastMatches("TE", s)) allowOthers = true;
            if (TokenUtils.fastMatches("NO", s)) allowOthers = false;
            if (TokenUtils.fastMatches("PR", s)) this.writeProximityMarkersPerApplication = true;
            if (TokenUtils.fastMatches("DT", s)) this.y2kFix = false;
            //FP is already default.if (TokenUtils.fastMatches("FP", s)) return "fast-phrase-indexing";
            if (TokenUtils.fastMatches("SW", s)) this.useStopWords = true;
        }
        this.allowOthersToIndex = allowOthers;

        //We are assuming the default behavior is 'TF,TE' instead of 'PF,TE'
        if (this.fieldAnalyzer == null) this.fieldAnalyzer = new FolioEnuAnalyzer();
    }

    // 1) analyze each application as a separate term. Use lowercase normalization and trim


    //Notes - single quotes just escape special characters, they do not denote a phrase search.
    // Single quotes cannot be used to determine whether to analyze the text as a single token.
	/*
	

	*/

}


/*
	TF,TE	Normal
	PF,TE	Phrase
	PF,FP - Phrase, and fast phrase
	No	Unindexed
	TF	Field Only
	PF	Field Only as Phrase

Code	Explanation	
TF	Term Field indexing. Individual terms in the field are indexed separately in the Field's index.	
PF	Phrase Field indexing. The entire contents of the field are indexed as a phrase in the Field's index.	
TE	Term Enclosing indexing. Individual terms in the field are indexed separately in the infobase's index.	
NO	Not indexed. Terms in the field will not be found if searched for in the infobase. (This option may not be used in conjunction with other options.)	
PR	Proximity. All terms in a field primitive must be found within a single application of the field to register a hit. Most often used with TE and TF.	
DT	Used only with Date fields. Controls how dates specified with a 2-number abbreviation (95, 96, 97, etc.) are handled. If this option is used, dates less than or equal to 49 are assumed to be after the year 2000; dates greater than or equal to 50 are assumed to be before the year 2000 (such as 1995). If this option is not used, all dates are assumed to be in the 1900s.	
FP	Fast Phrase. Enables the field for fast phrase searching.	
SW	Stop Words. Uses stop words to reduce the size of the index for fields when fast phrase is enabled.	
Bug Notice:

Indexing Options
There are two sets of indexing options. One set applies to text fields only. The other set applies to date fields only.
Text Fields
Indexing options allow you to specify how information in a text field may be searched in the finished infobase. (The indexing options extend the functionality of the IX+ and IX- options provided in the 3.1 version of flat file.)
There are basically three types of indexing options: Field Indexing, Enclosing Indexing, and No Indexing. Field Indexing adds the terms in the field application to the field's index. This allows the terms to be found in a field search (such as Field X:dog). Enclosing Indexing adds the terms in the field application to the infobase index. This allows the terms to be found during a normal query (such as a search for the word dog). No Indexing turns indexing off completely; the terms in the field application cannot be found in any search.
In addition, you may specify how terms are indexed in the Field Indexing and Field Enclosing options. Terms may be indexed as individual terms and searched as individual terms (this is what is described in the preceding paragraph). Terms within a field application may also be indexed as a phrase. When terms are indexed as a phrase, then all of the terms in the field application are considered to be one word for searching purposes. For example, if International Business Machines is indexed as a phrase, the single term International Business Machines appears in the Word List in the Advanced Query dialog (rather than three separate terms).
Finally, text fields may also set a proximity for searches. If you use the Proximity (PR) option, then queries for terms in the field require that all of the terms exist in the same application of the field for a hit to register. For example, a search for [Field Presidents: Washington Adams Lincoln] would require that all three of these terms be found in the same field application. See Notes on the Proximity (PR) Indexing Option for more information. 
These options may be used in conjunction for different effects. The default is Term Field (TF) and Term Enclosing (TE) (if no options are specified, these are used by default). This combination allows the terms in the field to be found both within the field and within the infobase as a whole.
Use all of the options (except the No Indexing (NO) option) to index terms both as phrases and as individual terms.
Use Term Field (TF) or Phrase Field (PF) alone to force the user to search in the field to find the term.
Using Term Enclosing (TE) alone does not make much sense but may be done. Using this alone allows users to find the information in the field in a general search of the infobase, but not when they search within the field. This option is normally used in conjunction with PF.
Note: While any combination of these options may be specified, the following are those allowed to be set from within Folio Views:

	TF,TE	Normal
	PF,TE	Phrase
	PF,FP - Phrase, and fast phrase
	No	Unindexed
	TF	Field Only
	PF	Field Only as Phrase

If no indexing options are set, the Normal is used (TF,TE).

*/