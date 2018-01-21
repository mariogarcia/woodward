package woodward.plan

import java.util.stream.Stream

import woodward.Article
import woodward.Category
import woodward.ArticleHolder
import woodward.util.Domain

/**
 * @since 0.1.0
 */
@Domain(copyWith = true)
class ArticlesInPlan implements FilteredPlan<Article> {

  /**
   * @since 0.1.0
   */
  String uri

  /**
   * @since 0.1.0
   */
  String title

  /**
   * @since 0.1.0
   */
  String category

  /**
   * @param title
   * @return
   * @since 0.1.0
   */
  ArticlesInPlan byTitle(String title) {
    return this.copyWith(title: title)
  }

  /**
   * @param category
   * @return
   * @since 0.1.0
   */
  ArticlesInPlan byCategory(String category) {
    return this.copyWith(category: category)
  }

  /**
   * @return
   * @since 0.1.0
   */
  Collection<Article> all() {
    return new CategoryPlan(uri: uri)
      .byName(category)
      .all()
      .parallelStream()
      .flatMap(ArticlesInPlan.extractArticles())
      .map(ArticlesInPlan.rehydrateArticle())
      .filter(ArticlesInPlan.articleTitleContains(title) as java.util.function.Predicate)
      .collect()
  }

  /**
   * @return
   * @since 0.1.0
   */
  static Closure<Stream<ArticleHolder>> extractArticles() {
    return { Category category ->
      category.articles.findAll().stream()
    }
  }

  /**
   * @return
   * @since 0.1.0
   */
  static Closure<Article> rehydrateArticle() {
    return { ArticleHolder holder ->
      return new ArticlePlan(uri: holder.link).get()
    }
  }

  /**
   * @param reference
   * @return
   * @since 0.1.0
   */
  static Closure<Boolean> articleTitleContains(String reference) {
    return { Article article ->
      reference ? article.title.toLowerCase() ==~ reference.toLowerCase() : true
    }
  }

  /**
   * @return
   * @since 0.1.0
   */
  Article single() {
    return all().find()
  }
}
