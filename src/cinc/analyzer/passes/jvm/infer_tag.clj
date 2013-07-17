(ns cinc.analyzer.passes.jvm.infer-tag
  (:require [cinc.analyzer.utils :refer [arglist-for-arity]]
            [cinc.analyzer.jvm.utils :refer [convertible?]])
  (:import (clojure.lang IPersistentVector IPersistentMap
                         IPersistentSet ISeq Keyword Var
                         Symbol)
           java.util.regex.Pattern))

(defmulti infer-constant-tag :op)
(defmulti -infer-tag :op)

(defmethod infer-constant-tag :vector
  [ast]
  (assoc ast :tag IPersistentVector))

(defmethod infer-constant-tag :map
  [ast]
  (assoc ast :tag IPersistentMap))

(defmethod infer-constant-tag :set
  [ast]
  (assoc ast :tag IPersistentMap))

(defmethod infer-constant-tag :seq
  [ast]
  (assoc ast :tag ISeq))

(defmethod infer-constant-tag :class
  [{:keys [form] :as ast}]
  (assoc ast :tag form))

(defmethod infer-constant-tag :keyword
  [ast]
  (assoc ast :tag Keyword))

(defmethod infer-constant-tag :symbol
  [ast]
  (assoc ast :tag Symbol))

(defmethod infer-constant-tag :string
  [ast]
  (assoc ast :tag String))

;; need to specialize
(defmethod infer-constant-tag :number
  [ast]
  (assoc ast :tag Number))

(defmethod infer-constant-tag :type
  [{:keys [form] :as ast}]
  (assoc ast :tag (class form)))

(defmethod infer-constant-tag :record
  [{:keys [form] :as ast}]
  (assoc ast :tag (class form)))

(defmethod infer-constant-tag :char
  [ast]
  (assoc ast :tag Character))

(defmethod infer-constant-tag :regex
  [ast]
  (assoc ast :tag Pattern))

(defmethod infer-constant-tag :the-var
  [ast]
  (assoc ast :tag Var))

(defmethod infer-constant-tag :const
  [{:keys [op type form] :as ast}]
  (if (not= :unknown type)
    (assoc (infer-constant-tag (assoc ast :op type))
      :op op)
    (assoc ast :tag (class form))))

(defmethod -infer-tag :binding
  [{:keys [init] :as ast}]
  (if init
    (merge ast
           (when-let [tag (:tag init)]
             {:tag tag})
           (when-let [arglists (:arg-lists init)]
             {:arg-lists arglists}))
    ast))

(defmethod -infer-tag :local
  [{:keys [init] :as ast}]
  (if init
        (merge ast
           (when-let [tag (:tag init)]
             {:tag tag})
           (when-let [arglists (:arg-lists init)]
             {:arg-lists arglists}))
        ast))

(defmethod -infer-tag :var
  [{:keys [var] :as ast}]
  (let [{:keys [dynamic tag arg-lists]} (meta var)]
    (if (not dynamic)
      (merge ast
             (when tag {:tag tag})
             (when arg-lists {:arg-lists arg-lists}))
      ast)))

(defmethod -infer-tag :def
  [{:keys [init var] :as ast}]
  (let [ast (assoc ast :tag Var)]
    (if-let [arglists (:arg-lists init)]
      (assoc ast :arg-lists arglists)
      ast)))

(defmethod -infer-tag :if
  [{:keys [then else] :as ast}]
  (let [[then-tag else-tag] (mapv :tag [then else])]
    (cond

     (or (nil? (:form else)) (nil? (:form then))
         (= then-tag else-tag))
     (assoc ast :tag (or then-tag else-tag))

     (convertible? else-tag then-tag)
     (assoc ast :tag then-tag)

     (convertible? then-tag else-tag)
     (assoc ast :tag else-tag)

     :else ast)))

(defmethod -infer-tag :new
  [{:keys [maybe-class class] :as ast}]
  (assoc ast :tag (or class maybe-class)))

(defmethod -infer-tag :do
  [{:keys [ret] :as ast}]
  (if-let [tag (:tag ret)]
    (assoc ast :tag tag)
    ast))

(defmethod -infer-tag :let
  [{:keys [body] :as ast}]
  (if-let [tag (:tag body)]
    (assoc ast :tag tag)
    ast))

(defmethod -infer-tag :letfn
  [{:keys [body] :as ast}]
  (if-let [tag (:tag body)]
    (assoc ast :tag tag)
    ast))

(defmethod -infer-tag :loop
  [{:keys [body] :as ast}]
  (if-let [tag (:tag body)]
    (assoc ast :tag tag)
    ast))

(defmethod -infer-tag :fn-method
  [{:keys [form body params] :as ast}]
  (let [tag (or (:tag (meta (first form)))
                (:tag body))
        ast (if tag
              (assoc ast :tag tag)
              ast)]
    (assoc ast
      :arglist (with-meta (mapv :name params)
                 {:tag tag}))))

(defmethod -infer-tag :fn
  [{:keys [methods] :as ast}]
  (assoc ast :arg-lists (mapv :arglist methods)))

(defmethod -infer-tag :try
  [{:keys [body catches] :as ast}]
  (if-let [body-tag (:tag body)]
    (if-let [catches-tags (seq (filter identity (map (comp :tag :body) catches)))]
      (if (every? = (conj catches-tags body-tag))
        (assoc ast :tag body-tag)
        ast)
      (assoc ast :tag body-tag)) ;; or should it infer nothing? we need to differenciate between nil and not there
    ast))

(defmethod -infer-tag :invoke
  [{:keys [fn args] :as ast}]
  (if (#{:var :local :fn} (:op fn))
    (let [argc (count args)
          arglist (arglist-for-arity fn argc)]
      (if-let [tag (or (:tag (meta arglist)) ;; ideally we would select the fn-method
                       (:tag fn))]
        (assoc ast :tag tag)
        ast))
    ast))

(defmethod -infer-tag :reify
  [{:keys [interfaces methods] :as ast}]
  (let [tags (conj interfaces Object)]
   (assoc (assoc ast :methods
                 (reduce #(conj % (assoc %2 :interfaces tags)) [] methods))
     :tag tags)))

(defmethod -infer-tag :deftype
  [{:keys [interfaces methods] :as ast}]
  (let [tags (conj interfaces Object)]
   (assoc (assoc ast :methods
                 (reduce #(conj % (assoc %2 :interfaces tags)) [] methods))
     :tag tags)))

(defmethod -infer-tag :method
  [{:keys [form body params] :as ast}]
  (let [tag (or (:tag (meta (first form)))
                (:tag body))]
    (if tag
      (assoc ast :tag tag)
      ast)))

(defmethod infer-constant-tag :default [ast] ast)
(defmethod -infer-tag :default [ast] ast)

(defn infer-tag
  [{:keys [tag form name] :as ast}]
  (if tag
    ast
    (if-let [tag (:tag (meta name))]
      (assoc ast :tag tag)
      (if-let [form-tag (:tag (meta form))]
        (assoc ast :tag form-tag)
        (-infer-tag ast)))))