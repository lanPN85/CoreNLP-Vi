package edu.stanford.nlp.time;

import java.util.Properties;

import edu.stanford.nlp.ling.tokensregex.Env;
import edu.stanford.nlp.util.PropertiesUtils;

/**
 * Various options for using time expression extractor
 *
 * @author Angel Chang
 */
public class Options {

  public enum RelativeHeuristicLevel { NONE, BASIC, MORE }

  // Whether to mark time ranges like from 1991 to 1992 as one timex
  // or leave it separate
  public boolean markTimeRanges = false;
  // Whether include non timex3 temporal expressions
  boolean restrictToTimex3 = false;
  // Heuristics for determining relative time
  // level 1 = no heuristics (default)
  // level 2 = basic heuristics taking into past tense
  // level 3 = more heuristics with since/until
  RelativeHeuristicLevel teRelHeurLevel = RelativeHeuristicLevel.NONE;
  // Include nested time expressions
  boolean includeNested = false;
  // Create range for all temporals and include range attribute in timex annotation
  boolean includeRange = false;
  // Look for document date in the document text (if not provided)
  boolean searchForDocDate = false;
  // TODO: Add default country for holidays and default time format
  // would want a per document default as well
  String grammarFilename = null;
  Env.Binder[] binders = null;

  static final String DEFAULT_GRAMMAR_FILES = "models/sutime/defs.sutime.txt,edu/stanford/nlp/models/sutime/english.sutime.txt,edu/stanford/nlp/models/sutime/english.holidays.sutime.txt";
  static final String[] DEFAULT_BINDERS = { "edu.stanford.nlp.time.JollyDayHolidays" };
  //static final String[] DEFAULT_BINDERS = { };

  boolean verbose = false;

  public Options()
  {
  }

  public Options(String name, Properties props)
  {
    includeRange = PropertiesUtils.getBool(props, name + ".includeRange",
                                           includeRange);
    markTimeRanges = PropertiesUtils.getBool(props, name + ".markTimeRanges",
                                             markTimeRanges);
    includeNested = PropertiesUtils.getBool(props, name + ".includeNested",
                                            includeNested);
    restrictToTimex3 = PropertiesUtils.getBool(props, name + ".restrictToTimex3",
            restrictToTimex3);
    teRelHeurLevel = RelativeHeuristicLevel.valueOf(
                       props.getProperty(name + ".teRelHeurLevel",
                                         teRelHeurLevel.toString()));
    verbose = PropertiesUtils.getBool(props, name + ".verbose", verbose);

    grammarFilename = props.getProperty(name + ".rules", DEFAULT_GRAMMAR_FILES);

    searchForDocDate = PropertiesUtils.getBool(props, name + ".searchForDocDate", searchForDocDate);

    String binderProperty = props.getProperty(name + ".binders");
    int nBinders;
    String[] binderClasses;
    if (binderProperty == null) {
      nBinders = DEFAULT_BINDERS.length;
      binderClasses = DEFAULT_BINDERS;
    } else {
      nBinders = PropertiesUtils.getInt(props, name + ".binders", 0);
      binderClasses = new String[nBinders];
      for (int i = 0; i < nBinders; ++i) {
        String binderPrefix = name + ".binder." + (i + 1);
        binderClasses[i] = props.getProperty(binderPrefix);
      }
    }
    if (nBinders > 0 && System.getProperty("STS") == null) {
      binders = new Env.Binder[nBinders];
      for (int i = 0; i < nBinders; i++) {
        int bi = i+1;
        String binderPrefix = name + ".binder." + bi;
        try {
          Class binderClass = Class.forName(binderClasses[i]);
          binderPrefix = binderPrefix + ".";
          binders[i] = (Env.Binder) binderClass.newInstance();
          binders[i].init(binderPrefix, props);
        } catch (Exception ex) {
          throw new RuntimeException("Error initializing binder " + bi, ex);
        }
      }
    }
  }
}
