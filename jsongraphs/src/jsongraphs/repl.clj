(ns jsongraphs.repl)

;; https://www.postgresql.org/docs/current/datatype-json.html
;; https://github.com/jsongraph/json-graph-specification
;; https://cljdoc.org/d/seancorfield/next.jdbc/1.0.409/doc/getting-started/tips-tricks#working-with-json-and-jsonb
;; https://www.metosin.fi/blog/faster-json-processing-with-jsonista/

(require '[next.jdbc :as jdbc])
(require '[next.jdbc.date-time :as dt])
(require '[next.jdbc.prepare :as prepare])
(require '[next.jdbc.result-set :as rs])
(import '(org.postgresql.util PGobject))

(def db {:dbtype   "postgresql"
         :dbname   "jsongraph"
         :port     5432
         :user     "jsongraph"
         :password "jsongraph"})

(def ds (jdbc/get-datasource db))

;; Create json table for storing graphs
;; https://www.postgresql.org/docs/current/datatype-json.html

(jdbc/execute! ds ["
-- CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";
DROP TABLE jsongraph;
CREATE TABLE jsongraph (
 graph jsonb
);
CREATE INDEX idxgin ON jsongraph using GIN(graph)
"])

;; Insert some graphs
;; https://github.com/jsongraph/json-graph-specification



;; https://www.metosin.fi/blog/faster-json-processing-with-jsonista/
(require '[jsonista.core :as json])
;; :decode-key-fn here specifies that JSON-keys will become keywords:
(def mapper (json/object-mapper {:decode-key-fn keyword}))
(def ->json json/write-value-as-string)
(def <-json #(json/read-value % mapper))
(defn ->pgobject
  "Transforms Clojure data to a PGobject that contains the data as
  JSON. PGObject type defaults to `jsonb` but can be changed via
  metadata key `:pgtype`"
  [x]
  (let [pgtype (or (:pgtype (meta x)) "jsonb")]
    (doto (PGobject.)
      (.setType pgtype)
      (.setValue (->json x)))))

(defn <-pgobject
  "Transform PGobject containing `json` or `jsonb` value to Clojure
  data."
  [^org.postgresql.util.PGobject v]
  (let [type  (.getType v)
        value (.getValue v)]
    (if (#{"jsonb" "json"} type)
      (with-meta (<-json value) {:pgtype type})
      value)))
(extend-protocol prepare/SettableParameter
  clojure.lang.IPersistentMap
  (set-parameter [m s i]
    (.setObject s i (->pgobject m)))

  clojure.lang.IPersistentVector
  (set-parameter [v s i]
    (.setObject s i (->pgobject v))))

;; if a row contains a PGobject then we'll convert them to Clojure data
;; while reading (if column is either "json" or "jsonb" type):
(extend-protocol rs/ReadableColumn
  org.postgresql.util.PGobject
  (read-column-by-label [^org.postgresql.util.PGobject v _]
    (<-pgobject v))
  (read-column-by-index [^org.postgresql.util.PGobject v _2 _3]
    (<-pgobject v)))

(def file (java.io.File. "resources/examples/car_graphs.json"))
(def file (java.io.File. "resources/examples/usual_suspects.json"))
(def file (java.io.File. "resources/examples/les_miserables.json"))
(def file (java.io.File. "resources/examples/empty_test.json"))


(jdbc/execute! ds ["
INSERT INTO jsongraph VALUES (?::jsonb)" (json/write-value-as-string (json/read-value (slurp file)))])



;; Query graph
;; https://www.postgresql.org/docs/12/functions-json.html

(jdbc/execute! ds ["
SELECT jsonb_pretty(graph)
FROM jsongraph
"])


(jdbc/execute! ds ["
SELECT graph->'graph'->'label'
FROM jsongraph
"])

;; using JSON path queries
(jdbc/execute! ds ["
SELECT jsonb_path_query(graph, '$.graph.nodes')
FROM jsongraph
"])

;; jsongraph modelled as clojure datastructure
(def application-graph
  {"graph" {"id"    "2322323"
            "type"  "application-graph"
            "nodes" {"APPLICATION-1" {"metadata" {"applicationType" "CONSUMER"
                                                  "country-iso2"    "GB"}
                                      "label"    "APP-1"}
                     "APPLICANT-1"   {"metadata" {"applicantType" "PERSON"
                                                  "role"          "MAIN_APPLICANT"}
                                      "label"    "APPLICANT-1"}
                     "APPLICANT-2"   {"metadata" {"applicantType"            "PERSON"
                                                  "role"                     "CO_APPLICANT"
                                                  "relationtToMainApplicant" "Spouse"}
                                      "label"    "APPLICANT_2"}
                     "CRB-1"         {"label"    "CRB-1"
                                      "metadata" {"bureauType" "GB_EQUIFAX_CONSMER"}}
                     "CRB-2"         {"label"    "CRB-2"
                                      "metadata" {"bureauType" "GB_EXPERIAN_CONSUMER"}}}
            "edges" []}})

(jdbc/execute! ds ["
INSERT INTO jsongraph VALUES (?::jsonb)" (json/write-value-as-string application-graph)])
