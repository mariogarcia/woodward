package woodward

import spock.lang.Specification

class LibraryTest extends Specification {

  def "someLibraryMethod returns true"() {
    when:
//      Article article = W.readArticle("http://www.elmundo.es/cataluna/2017/12/19/5a3986a4268e3ed7478b45f5.html")
//      Article article = W.readArticle("https://www.thesun.co.uk/motors/5163233/cops-seize-car-being-driven-home-from-work-as-its-driver-wasnt-insured-for-commuting/")
        Article article = W.readArticle("http://edition.cnn.com/2017/12/19/politics/republican-tax-plan-vote/index.html")

//        Article article = W.readArticle("https://medium.com/@laurelcarney/failure-peace-and-purpose-in-the-last-jedi-8ab51a00ab16")
//        Article article = W.readArticle("https://www.genbeta.com/a-fondo/innovacion-seguridad-y-velocidad-los-mejores-navegadores-de-2017")
//    Article article = W.readArticle("https://es.reuters.com/article/topNews/idESKBN1EE1D4-OESTP")

      println(article)

    then:
      article.title
      article.text
      article.authors
  }
}
