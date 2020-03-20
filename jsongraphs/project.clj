(defproject jsongraphs "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure       "1.10.1"]
                 [seancorfield/next.jdbc    "1.0.6"]
                 [com.zaxxer/HikariCP       "3.4.2"]
                 [org.postgresql/postgresql "42.2.10"]
                 [metosin/jsonista          "0.2.5"]]
  :repl-options {:init-ns jsongraphs.core})
