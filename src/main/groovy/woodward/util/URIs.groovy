package woodward.util

import org.jsoup.nodes.Element

/**
 * Set of functions to deal with {@link URI} instances
 *
 * @since 0.1.0
 */
class URIs {

  /**
   * Parses a given uri string and returns an instance of {@link URI}. If
   * the uri is not valid of there is a problem while parsing it the method will
   * return an empty {@link Optional}
   *
   * @param uri the uri location as a string
   * @return an {@link Optional} instance
   * @since 0.1.0
   */
  static Optional<URI> parseURI(String uri) {
    URI parsedUri = null

    try {
      parsedUri = URI.create(uri)
    } catch(IllegalArgumentException e) {
      return Optional.empty()
    }

    return Optional.of(parsedUri)
  }

  /**
   * Returns the number of path fragments found in the uri passed
   * as parameter
   *
   * @param uri an optional that may contain an instance of {@link
   * URI}
   * @return the number of path fragments found, 0 if none are found
   * @since 0.1.0
   */
  static Integer getPathSize(Optional<URI> uri) {
    return uri
      .map { it.path.split('/').findAll().size() }
      .orElse(0)
  }

  /**
   * Finds out if the uri passed as parameter has a query string or
   * not
   *
   * @param uri an instance of {@link Optional} potentially containing
   * an uri
   * @return true if the uri had a query string false otherwise
   * @since 0.1.0
   */
  static Boolean hasQuery(Optional<URI> uri) {
    return uri
      .map { it.query }
      .isPresent()
  }

  /**
   * Sometimes links in a page are relative to the domain. This method
   * will take any relative link and create an complete URI.
   *
   * @param link possible relative link
   * @param element instance of {@link Element} needed because we are
   * taking the domain information from it
   * @return a string containing a complete uri
   * @since 0.1.0
   */
  static String repairLink(String link, Element element) {
    Optional<URI> baseUri = parseURI(element.baseUri())
    Optional<URI> fixedUri = parseURI(link)
      .filter(URIs.&hasNotScheme)
      .flatMap { uri ->
         baseUri
          .filter(URIs.&hasScheme)
          .map { "${it.scheme}:${uri}" }
      }

    return fixedUri.orElse(link)
  }

  /**
   * Finds out whether the uri passed as parameter has its scheme part
   * or not
   *
   * @param uri an instance of {@link URI}
   * @return true if the uri has its scheme part, false otherwise
   * @since 0.1.0
   */
  static Boolean hasScheme(URI uri) {
    if (!uri) return false

    return uri?.scheme != null
  }

  /**
   * Finds out whether the uri passed as parameter lacks its scheme
   * part or not
   *
   * @param uri an instance of {@link URI}
   * @return true if the uri lacks of scheme part, false otherwise
   * @since 0.1.0
   */
  static Boolean hasNotScheme(URI uri) {
    return !hasScheme(uri)
  }
}
