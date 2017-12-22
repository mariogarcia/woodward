package woodward.extractors

import org.jsoup.nodes.Document
import woodward.util.Misc

/**
 * This class contains functions to extract author names from
 * a given document
 *
 * @since 0.1.0
 */
class Authors {

  /*
   * @since 0.1.0
   */
  static final String SEPARATOR = " and "

  /*
   * Constant value representing a space character
   *
   * @since 0.1.0
   */
  static final String SPACE = " "

  /*
   * Map of common prepositions in different languages that normally
   * precede person names
   *
   * @since 0.1.0
   */
  static final Map<String, String> TO_REMOVE_FROM_AUTHORS = [
    'en': '[BbYy] ',
    'es': '[PpOoRr] ',
    'fr': '[PpAaRr] '
  ]

  /**
   * Extracts a list of author names found in the {@link Document}
   * passed as parameter
   *
   * @param document document source to get the authors from
   * @return a list of authors
   * @since 0.1.0
   */
  static List<String> extract(Document document) {
    def paths = [{-> byAttrName(document)},
                 {-> byElementClass(document)},
                 {-> byAttributeAndContent(document)}]


    return paths.findResult(Misc.&skipIfEmptyList)
  }

  /**
   * Only effective with english newspapers. It tries
   * to locate elements with a given attribute name and
   * a given attribute value
   *
   * @param document document source
   * @return a list of authors
   * @since 0.1.0
   */
  static List<String> byAttrName(Document document) {
    def attrs = ['name', 'rel', 'itemprop', 'class', 'id']
    def values = ['author', 'byline', 'dc.creator']

    String authors = [attrs, values]
      .combinations()
      .findResults(Authors.perCombination(document))
      .unique()
      .join(SEPARATOR)

    return splitAuthors(authors)
  }

  /**
   * Returns a function used to get a list of authors from a given
   * tuple of attribute name and value.
   *
   * @param document {@link Document} to get the tuples from
   * @return a {@link Closure} receiving a list and returning a list
     of strings
   * @since 0.1.0
   */
  static Closure<String> perCombination(Document document) {
    return  { List<String> attrAndValue ->
        def attributeName = attrAndValue.first()
        def attributeValue = attrAndValue.last()

        def authors = document
          .getElementsByAttribute(attributeName)
          .findAll { it.attr(attributeName).toLowerCase() == attributeValue }
          .findResult { it.attr('content') }

        return authors
    }
  }

  /**
   * This is a hardcoded strategy looking for elements with specific
   * class names
   *
   * @param document document source
   * @return a list of authors
   * @since 0.1.0
   */
  static List<String> byElementClass(Document document){
    def classNames = ['author-name', 'article-author-link']
    def authors = classNames.findResult { name ->
      document.select(".$name").text() ?: null
    }

    return splitAuthors(authors)
  }

  /**
   * This strategy looks for any element with a class attribute
   * containing author and also containing the litteral By in its
   * content.
   *
   * An idea could be to replace this strategy by another
   * checking the attribute name and then checking that the content
   * has one or several person names using NLP
   *
   * @param document document source
   * @return a list of authors
   * @since 0.1.0
   */
  static List<String> byAttributeAndContent(Document document) {
    String authors = document
      .select("[class*=author]:contains(By)")
      .collect { it.text().trim() }
      .unique()
      .join(SEPARATOR)

    return splitAuthors(authors)
  }

  /**
   * Removes common prepositions preceding names from the string
   * passed as parameter
   *
   * @param authors string containing name/s of authors
   * @return a string without common prepositions
   * @since 0.1.0
   */
  static String removePrefixes(String authors) {
    return TO_REMOVE_FROM_AUTHORS
      .entrySet()
      .inject(authors){ acc, val -> acc.replaceAll(val.value, SPACE) }
  }


  /**
   * Given a string containing several authors, it splits the names by
   * a given separator forming a list
   *
   * @param authors a string containing all authors
   * @return a list of authors
   * @since 0.1.0
   */
  static List<String> splitAuthors(String authors) {
    if (!authors) return []

    return removePrefixes(authors)
      .split(SPACE)
      .findAll()
  }
}
