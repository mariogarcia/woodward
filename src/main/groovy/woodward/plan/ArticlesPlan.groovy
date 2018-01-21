package woodward.plan

import woodward.Article

class ArticlesPlan implements Plan<Collection<Article>> {

  List<String> uris

  /**
   * This method blocks until the value is computed
   *
   * return the result of the plan
   * @since 0.1.0
   */
  Collection<Article> get() {
    return uris
      .parallelStream()
      .map { new ArticlePlan(uri: it).get() }
      .collect()
  }
}
