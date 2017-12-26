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

    return extractNames(document)
      .collect(Categories.&loadCategoryArticles)
  }

  /**
   * Extracts a list of {@link Category} names from the {@link
   * Document} passed as parameter
   *
   * @param document document we want the categories from
   * @return a list of category names
   * @since 0.1.0
   */
  static List<String> extractNames(Document document) {
    log.debug "extracting category names from document"

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

    return new Category(name: name, link: href)
  }

  /**
   * Loads the articles of the {@link Category} passed as parameter
   * and returns a new {@link Category} instance with its articles
   * loaded
   *
   * @param category without the articles loaded
   * @return a new instance with the category articles loaded
   * @since 0.1.0
   */
  static Category loadCategoryArticles(Category category) {
    return category.copyWith(articles: getArticlesFrom(category.link))
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
   * Returns a function to filter categories by the
   * regex passed as parameter
   *
   * @param name name to filter categories by
   * @return a function to filter categories by name
   * @since 0.1.0
   */
  static Closure<Boolean> byNameIfPresent(String name) {
    Optional<String> categoryName = Optional.ofNullable(name)

    return !categoryName.isPresent() ?
      { Category category -> true } :
      { Category category -> category.name ==~ name }
  }

  /**
   * Depending on the parameter passed it will return a function that
   * loads a category articles or the identity function
   *
   * @param load whether to load or not category's articles
   * @return a function conditionally loading category's articles
   * @since 0.1.0
   */
  static Closure<Category> loadArticlesIf(Boolean load) {
    return load ?
      Categories.&loadCategoryArticles :
      Misc.identity()
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
