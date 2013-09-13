(ns cinc.analyzer.passes.collect
  (:require [cinc.analyzer.utils :refer [protocol-node? update!]]
            [cinc.analyzer.passes :refer [postwalk]]))

(def ^:private ^:dynamic *collects*
  {:constants           {}
   :vars                {} ;; is this actually needed?
   :closed-overs        {}
   :protocol-callsites #{}
   :keyword-callsites  #{}})

(defn -register-constant
  [form tag type]
  (or ((:constants *collects*) form)
      (let [id (count (:constants *collects*))]
        (update! *collects* assoc-in [:constants form] {:id   id
                                                        :tag  tag
                                                        :val  form
                                                        :type type})
        id)))

(defn -collect-constants
  [{:keys [op form tag type] :as ast}]
  (if (and (= op :const)
           (not= type :nil)
           (not= type :boolean))
    (let [id (-register-constant form tag type)]
      (assoc ast :id id))
    ast))

(defn -collect-vars
  [{:keys [op var] :as ast}]
  (if (#{:def :var :the-var} op)
    (let [id (or ((:vars *collects*) var)
                 (let [id (-register-constant var clojure.lang.Var :var)]
                   (update! *collects* assoc-in [:vars var] id)
                   id))]
      (assoc ast :id id))
    ast))

(defn -collect-callsites
  [{:keys [op] :as ast}]
  (when (#{:keyword-invoke :invoke} op)
    (let [f (:fn ast)]
      (cond
       (and (= :var (:op f))
            (protocol-node? (:var f)))
       (update! *collects* update-in [:protocol-callsites] conj (:name f))

       (= :keyword-invoke op)
       (update! *collects* update-in [:keyword-callsites] conj (:form f)))))
  ast)

(defmulti -collect-closed-overs :op)
(defmethod -collect-closed-overs :default [ast] ast)

(defmethod -collect-closed-overs :local
  [{:keys [op name] :as ast}]
  (update! *collects* update-in [:closed-overs] assoc name ast)
  ast)

(defmethod -collect-closed-overs :binding
  [{:keys [init name tag] :as ast}]
  (update! *collects* update-in [:closed-overs] dissoc name)
  (when init
    (-collect-closed-overs init)) ;; since we're in a postwalk, a bit of trickery is necessary
  ast)

(defmethod -collect-closed-overs :deftype
  [{:keys [fields] :as ast}]
  (update! *collects* assoc :closed-overs (zipmap (mapv :name fields) fields))
  ast)

(defmethod -collect-closed-overs :fn-method
  [{:keys [params] :as ast}]
  (update! *collects* update-in [:closed-overs]
           #(apply dissoc % (mapv :name params)))
  ast)

(defmethod -collect-closed-overs :method
  [{:keys [params] :as ast}]
  (update! *collects* update-in [:closed-overs]
           #(apply dissoc % (mapv :name params)))
  ast)

(defmethod -collect-closed-overs :fn
  [{:keys [name] :as ast}]
  (update! *collects* update-in [:closed-overs] dissoc name)
  ast)

(defn collect-fns [what]
  (case what
    :constants    -collect-constants
    :vars         -collect-vars
    :closed-overs -collect-closed-overs
    :callsites    -collect-callsites
    nil))

(defn collect [& what]
  (fn [{:keys [op env] :as ast}]
    (if (#{:fn :deftype :reify} op)
      (binding [*collects* *collects*]
        (let [f (apply comp (filter identity (mapv collect-fns what)))]
          (into (postwalk ast f)
                *collects*)))
      ast)))
