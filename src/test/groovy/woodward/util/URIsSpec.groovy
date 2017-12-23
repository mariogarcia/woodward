package woodward.util

import spock.lang.Specification

/**
 * Tests all functions dealing with URIs
 *
 * @since 0.1.0
 */
class URIsSpec extends Specification {

  def 'parse an URI'() {
    when: 'parsing a given uri'
      Optional<URI> optional = URIs.parseURI(uri)

    then: 'we should get an optional'
      optional.isPresent() == isPresent

    where: 'possible URIs are'
    uri                              ||  isPresent
    'http://www.samplenewspaper.com' ||   true
    '//samplenewspaper.com'          ||   true
    'samplenewspaper'                ||   true
    'something else'                 ||   false
    'somethingw\\ith@wrong?_symbols' ||   false
  }
}
