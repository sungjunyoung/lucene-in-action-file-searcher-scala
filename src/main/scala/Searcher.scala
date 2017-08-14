import java.io.File

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.{DirectoryReader, IndexReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{IndexSearcher, Query, TopDocs}
import org.apache.lucene.store.{Directory, FSDirectory}
import org.apache.lucene.util.Version

/**
  * Created by junyoung on 2017. 8. 14..
  */
object Searcher {

  def search(indexDir: String, q: String): Unit = {
    val dir:Directory = FSDirectory.open(new File(indexDir))
    val ir:IndexReader = DirectoryReader.open(dir)
    val is:IndexSearcher = new IndexSearcher(ir)

    val parser:QueryParser = new QueryParser(Version.LUCENE_46, "contents", new StandardAnalyzer(Version.LUCENE_46))
    val query:Query = parser.parse(q)

    val start:Long = System.currentTimeMillis()
    val hits:TopDocs = is.search(query, 10)
    val end:Long = System.currentTimeMillis()

    val totalHits = hits.totalHits
    val term = end - start
    println(s"Found $totalHits document(s) (in $term milliseconds) that matched query '$q' : ")

    for(scoreDoc <- hits.scoreDocs){
      val doc:Document = is.doc(scoreDoc.doc)
      println(doc.get("fullpath"))
    }

    // Deprecated
    // is.close()
  }

  def main(args: Array[String]): Unit = {

    // Check arguments
    if(args.length != 2){
      throw new IllegalArgumentException(s"Usage: sbt run-main Searcher <index dir> <query>")
    }

    val indexDir:String = args(0)
    val q:String = args(1)
    search(indexDir, q)

  }
}
