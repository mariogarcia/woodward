package woodward.extractors

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import woodward.Source
import woodward.Category
import woodward.ArticleHolder
import woodward.util.Log
import woodward.util.URIs
import woodward.util.Misc
import woodward.util.Network

/**
 * A category represents a group of articles sharing the same topic
 *
 * @since 0.1.0
 */
@Log
class Categories {

  /**
   * Extracts a list of {@link Category} from the {@link Document}
   * passed as parameter
   *
   * @param document document we want the categories from
   * @return a list of {@link Category} instances
   * @since 0.1.0
   */
  static List<Category> extract(Document document) {
    log.debug "extracting categories from document"

    def paths = [{-> fromPathFragment(document)},
                 {-> fromSubdomain(document)}]


    return paths.findResult(Misc.&skipIfEmptyList)
  }

  /**
   * First strategy to get all categories from a given
   * online media. It looks for the links having the
   * category name in the first path fragment. E.g. <br/><br/>
   *
   * http://www.somenewspaper.com/sports <br/><br/>
   *
   * @param doc the document to get the categories from
   * @return a list of {@link Category} instances or empty list if
   * none have been found
   * @since 0.1.0
   */
  static List<Category> fromPathFragment(Document doc) {
    log.debug "getting categories from uri path"

    URI uri = URI.create(doc.location())
    String host = uri.host - "www."
    String selector = "a[href*=${host}]"

    return doc
      .select(selector)
      .findAll(Categories.&isCategoryInPath)
      .collect(Categories.&extractCategoryFromPath)
  }

  /**
   * Extracts a single category from the document passed as parameter
   * with a specific name
   *
   * @param document the source document
   * @param category the name of the category we are looking for
   * @return an instance of type {@link Category}
   * @since 0.1.0
   */
  static Category extractCategory(Document document, String category) {
    URI uri = URI.create(document.location())
    String host = uri.host - "www."
    String selector = "a"

    return document
      .select(selector)
      .find(Categories.isCategoryInPathAndHasName(category))
      .collect(Categories.&extractCategoryFromPath)
      .find()
  }

  /**
   * Creates a predicate able to find those {@link Element} instances
   * that represents a category link and containing the category name
   * passed as parameter
   *
   * @param category the category name
   * @return a predicate to find a specific set of links
   * @since 0.1.0
   */
  static Closure<Boolean> isCategoryInPathAndHasName(String category) {
    return Misc.and(Categories.&isCategoryInPath, Categories.hasName(category))
  }

  /**
   * Whether the {@link Element} passed as parameter represents
   * a category link or not.
   *
   * A category link should follow certain rules:
   *
   * - The link name should have at most one word (Sports,
   *   Technology, U.S. Politics...etc)
   * - The uri should have at most two fragments in its path
   * - Should not have a query part
   *
   * @param link an instance of {@link Element}
   * @return whether the link is from a category or not
   * @since 0.1.0
   */
  static Boolean isCategoryInPath(Element link) {
    String repairedLink = URIs.repairLink(link.attr('href'), link)

    Optional<URI> uri = URIs.parseURI(repairedLink)
    Integer pathSize = URIs.getPathSize(uri)

    Boolean twoFragments = pathSize == 2
    Boolean oneFragment = pathSize == 1

    Boolean hasQuery = URIs.hasQuery(uri)
    Boolean hasNoun = hasCategoryNoun(link.text())

    Boolean cond1 = twoFragments && !hasQuery && hasNoun
    Boolean cond2 = oneFragment && hasNoun

    return cond1 || cond2
  }

  /**
   * Creates a {@link Category} instance from the {@link Element}
   * passed as parameter
   *
   * @param link an element representing an html link
   * @return an instance of type {@link Category}
   * @since 0.1.0
   */
  static Category extractCategoryFromPath(Element link) {
    def href = URIs.repairLink(link.attr('href'), link)
    def name = link.text()
    def articles = getArticlesFrom(href)

    return new Category(name: name, link: href, articles: articles)
  }

  /**
   * This stragegy tries to figure out the categories from
   * the subdomain name. E.g: <br/><br/>
   *
   * http://sports.somenewspaper.com <br/><br/>
   *
   * Here the category has to be taken from
   * **sports.somenewspaper.com**
   *
   * @param doc
   * @return
   * @since 0.1.0
   */
  static List<Category> fromSubdomain(Document doc) {
    log.debug "getting categories from subdomain name"

  }

  /**
   * Represents a predicate to find {@link Element} instances having in their
   * text the category passed as parameter
   *
   * Both element text and category name are transformed to lowercase and then
   * the function checks that the category passed as parameter is contained in the
   * link text
   *
   * @param category the name of the category
   * @return a predicate to check whether the category name is
   * contained in the element's text
   * @since 0.1.0
   */
  static Closure<Boolean> hasName(String category) {
    return { Element item ->
      return item
        .text()
        .toLowerCase()
        .trim()
        .contains(category.toLowerCase().trim())
    }
  }

  /**
   * A category normally is named by at most two words: Sports,
   * International, U.S Politics...
   *
   * @param sentence sentence where the possible category name is
   * @return whether there is at most two words (true) or more (false) to
   * name the category
   * @since 0.1.0
   */
  static Boolean hasCategoryNoun(String sentence) {
    return sentence
      .split(" ")
      .findAll()
      .size() <= 2
  }

  /**
   * Gathers all {@link ArticleHolder} found in the link passed as
   * parameter
   *
   * @param link link to get the article list from
   * @return a list of {@link ArticleHolder} instances
   * @since 0.1.0
   */
  static List<ArticleHolder> getArticlesFrom(String link) {
    Document categoryHtml = Network.getDocument(link)

    return categoryHtml
      .select('a')
      .findAll(Categories.belongToSameCategoryAs(link))
      .collect(Categories.&toArticleHolder)
      .sort { it.link.size() }
      .unique { a, b ->
        URI.create(a.link).path <=> URI.create(b.link).path
      }
  }

  /**
   * This function returns a predicate that can be used to check
   * whether a given {@link Element} representing a link, belongs to
   * the same category as the one represented by the string uri passed
   * as parameter
   *
   * @param categoryUri uri of the category
   * @return a predicate to check whether the {@link Element} passed
   * as parameter belongs to the same category by checking its URI
   * root path
   * @since 0.1.0
   */
  static Closure<Boolean> belongToSameCategoryAs(String categoryUri) {
    Optional<String> optionalURI = Optional.ofNullable(categoryUri)

    if (!optionalURI.isPresent()) {
      log.warn('category uri is not present, predicate {-> false } returned instead')

      return { Element element ->
        false
      }
    }

    String categoryPath = URIs.getRootPathFrom(optionalURI)

    return { Element element ->
      def link = URIs.repairLink(element.attr('href'), element)
      def sameDomain = URIs.haveSameDomain(categoryUri, link)
      def categoryInLink = link ==~ ".*${categoryPath}.*"

      return sameDomain && categoryInLink
    }
  }

  /**
   * Converts a given {@link Element} representing a potential
   * article to a {@link ArticleHolder}
   *
   * @param element an element representing an {@link ArticleHolder}
   * @return an instance of type {@link ArticleHolder}
   * @since 0.1.0
   */
  static ArticleHolder toArticleHolder(Element element) {
    String repairedLink = URIs.repairLink(element.attr('href'), element)

    return new ArticleHolder(link: repairedLink)
  }
}
