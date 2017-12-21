package woodward.nlp

import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import woodward.Utils

/**
 * @since 0.1.0
 */
class Models {

  /**
   * @since 0.1.0
   */
  static final NameFinderME NAME_FINDER = new NameFinderME(new TokenNameFinderModel(Utils.classpathAsURL("/en-ner-person.bin")))

}
