# Woodward

**Woodward** is a library focused on retrieving articles from online
newspapers. This project is named after [Bob
Woodward](https://en.wikipedia.org/wiki/Bob_Woodward) famous because
of his reporting with Carl Bernstein on Watergate

## Feature list

The following lists describe the current status of the things you can
actually do with **Woodward**

### Source

- [x] Retrieve front page categories
- [ ] Retrieve category's article pointers
- [ ] Retrieve front page articles

### Article

- [x] Retrieve an article by a plain url string
- [x] Retrieve an article by an article holder
- [x] Retrieve title
- [x] Retrieve text
- [x] Retrieve publish date as string
- [ ] Retrieve publish date as a date

## How to

### Gradle

TODO

### How to get a source

A source `woodward.Source` represents the front page of an online
newspaper. The following snippet retrieves the categories and the
article pointers found in a given site.

```groovy
import woodward.W

def source = W.readSource("http://www.cnn.com")

assert source.categories
assert source.articles
```

An article pointer is just a pointer represented by
`woodward.ArticlePointer`. It's a link pointing at a given article, to
get the full article you can use both the instance of
`woodward.ArticlePointer` or the url alone.

### How to get a specific Category

If you're only interested in a specific category, you can pass the
source url and the name of the category you're interested in.

```groovy
import woodward.W

def category = W.loadCategory("http://www.cnn.com", "Sports")

assert category.link
assert category.name
assert category.articles
```

### How to get an article (plain string)

```groovy
import woodward.W

def article = W.readArticle("http://edition.cnn.com/style/article/dubai-police-supercars/index.html")

assert article.title
assert article.text
assert article.publishDate
assert article.authors
```

### How to get an article (ArticlePointer)

```groovy
import woodward.W

def source = W.readSource("http://www.cnn.com")
def article = W.readArticle(source.articles.first())

assert article.title
assert article.text
assert article.publishDate
assert article.authors
```
