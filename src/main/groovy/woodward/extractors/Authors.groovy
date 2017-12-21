package woodward.extractors

import org.jsoup.nodes.Document
import opennlp.tools.util.Span
import woodward.Utils
import woodward.nlp.Models

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
   * @since 0.1.0
   */
  static final String SPACE = " "

  /*
   * @since 0.1.0
   */
  static final Map<String, String> TO_REMOVE_FROM_AUTHORS = [
    'en': '[BbYy] ',
    'es': '[PpOoRr] ',
    'fr': '[PpAaRr] '
  ]

  /**
   * @param document document source
   * @return a list of authors
   * @since 0.1.0
   */
  static List<String> extract(Document document) {
    def paths = [{-> byAttrName(document)},
                 {-> byElementClass(document)},
                 {-> byAttributeAndContent(document)}]


    return paths.findResult(Utils.&skipIfEmptyList)
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
   *
   * @param document
   * @return
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
   * @param authors
   * @return
   * @since 0.1.0
   */
  static String removePrefixes(String authors) {
    return TO_REMOVE_FROM_AUTHORS
      .entrySet()
      .inject(authors){ acc, val -> acc.replaceAll(val.value, " ") }
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

    String[] tokens = removePrefixes(authors).split(SPACE)

    return Models
      .NAME_FINDER
      .find(tokens)
      .findAll()
      .collect { Span span ->
        Boolean isJustOneWord = (span.end - span.start) == 1

        String name = isJustOneWord ?
          "${tokens[span.start]}".toString() :
          "${tokens[span.start]} ${tokens[span.end - 1]}".toString()
      }
  }
}
