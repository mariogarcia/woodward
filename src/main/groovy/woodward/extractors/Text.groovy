package woodward.extractors

import org.jsoup.nodes.Document
import woodward.util.Misc

/**
 * Fetchs the article's text
 *
 * @since 0.1.0
 */
class Text {

  /**
   * List of possible combinations of attribute an attribute's value
   * to get the title from
   *
   * @since 0.1.0
   */
  static final HAPPY_PATHS = [
    ['attribute': 'itemprop', 'value': 'articlebody'],
    ['attribute': 'class', 'value': 'article-content']
  ]

  /**
   * Extracts the title from the document passed as an argument
   *
   * @param document document to get the title from
   * @return the article's text
   * @since 0.1.0
   */
  static String extract(Document document) {
    def paths = [{-> HAPPY_PATHS.findResult(byHappyPath(document))},
                 {-> byImportance(document) }]


    paths.findResult(Misc.&skipIfEmptyList)
  }

  /**
   * This method returns a function {@link Closure}. This function
   * will be used for every possible combination where the title could
   * be found in.
   *
   * @param document to get the title from
   * @return a function to get the title from a given combination of
   * attribute-name + attribute-value
   * @since 0.1.0
   * @see Text#HAPPY_PATHS
   */
  static Closure byHappyPath(Document document) {
    return { Map<String, String> map ->
      return document
        .getElementsByAttribute(map.attribute)
        .findResult { element ->
        if (element.attr(map.attribute).toLowerCase() == map.value) {
          element.select("p").text()
        }
      }
    }
  }

  /**
   * Gets the article's title from a DIV element with certain
   * importance.
   *
   * @param document document we want the title from
   * @return the title found
   * @since 0.1.0
   */
  static String byImportance(Document document) {
    return document
      .select("div p:gt(2)")
      .find()
      .parent()
      .select("p")
      .text()
  }
}
