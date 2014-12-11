# BigBang: compose component/system-update(s) in component/start invocation time

![image](https://dl.dropboxusercontent.com/u/8688858/bigbang.png)

### Customize the way your stuartsierra/component system starts

Extracted from component/[README#customization](https://github.com/stuartsierra/component/blob/master/README.md#customization) :

> Both ```update-system``` and ```update-system-reverse``` take a function as an argument   
and call it on each component in the system. 
Along the way, they ```assoc``` in the updated dependencies of each component.   
The ```update-system``` function iterates over the components in dependency order   
 (a component will be called after its dependencies)....

.. then It's not really surprising  that  [component/start-system](https://github.com/stuartsierra/component/blob/master/src/com/stuartsierra/component.clj#L151) also uses ```component/update-system``` to call component/start to get the system started.

### component/update-system: recieves a system and a fn to return a new-updated-system
Let's look the stuartsierra doc and implementation
```clojure
(defn update-system
  "Invokes (apply f component args) on each of the components at
  component-keys in the system, in dependency order. Before invoking
  f, assoc's updated dependencies of the component."
  [system component-keys f & args]
  (let [graph (dependency-graph system component-keys)]
    (reduce (fn [system key]
              (assoc system key
                     (-> (get-component system key)
                         (assoc-dependencies system)
                         (try-action system key f args))))
            system
            (sort (dep/topo-comparator graph) component-keys))))

```

**Great logic and nothing strange at this side** ... Then, when we call [component/start-system](https://github.com/stuartsierra/component/blob/master/src/com/stuartsierra/component.clj#L151) there is actually a call to update-system that it's a reduction on the system that applies any fn to each component after injecting fresh dependencies. And... logically, **if we pass a system and a fn we get a new-updated-system**.

### but... what is the component definition?  
Extracted from the component/[README](https://github.com/stuartsierra/component/blob/master/README.md)
> For the purposes of this framework, a component is a collection of functions or procedures which **share some runtime state**.

For me this **"functions that share runtime state"** is the key that we need to understand in our clojure functional world. And I think that its meaning can easily improved with this extended started-component definition:

#### started-component (a component after component/start)
**a started** component is a collection of functions or procedures wich share some runtime state **produced in component/start** (possibly using other started components, also called dependencies) 

Examples of started components can be a database component with an open connection db, or a webserver component listening on a port opened.

#### joining component/update-system and component/start   

To understand these two fns together, let's find the differences of following two sequence calls:

```clojure
(-> system                                              ;;  {components}
    (component/update-system your-update-fn your-args)  ;;  {updated-components}
    (component/update-system component/start)           ;;  {started-updated-components}
    
(-> system                                              ;;  {components}
    (component/update-system component/start)           ;;  {started-components} 
    (component/update-system your-update-fn your-args)  ;;  {updated-started-components}
```

If we simplify the problem...
```clojure
;; case 1 {started-updated-components}

user> (def system {:a 1})
user> (def system-updated (assoc system :c 3))
user> (def system-started (assoc system-updated :b 2))
user>  (system-started :c)
=> :3

;; case 2 {updated-started-components}

user> (def system {:a 1})
user> (def system-started (assoc system :b 2))
user> (def system-updated (assoc system-started :c 3))
user> (nil? (system-started :c))
=> true
```

Although it is very obvius now (and of course in functional language too): **When we update our system with component/start we get the running system state and further updates over this running system state are not available to this started-system-value**

##  BigBang/expand: compose component/system-updates in component/start invocation time
BigBang goes further in this time distinction to apply updates and lets you compose your update functions just-before-start and just-after-start, meaning boths inside component/start invocation time

```bigbang/expand``` needs a common stuartsierra/system-map instance and a map with 2 keys ```:before-start :after-start``` and for each key a vector of bigbang actions as value

```clojure 
(defn expand
  [system-map {:keys [before-start after-start]}]
...)

```
 
##  BigBang Actions and Phases
An action is specified in a very similar way as you use [clojure.core/apply](http://clojuredocs.org/clojure.core/apply) but without using "apply" and enclosing it with brackets 
```clojure
[action-function action-arg0 action-arg1 action-arg2 ...]
```
BigBang actions are defined as common stuartsierra/update-system actions: at least receive the component instance to update (and anymore args) and have to return the component updated
```
(defn your-action-function [component & more]
....
;;=> actions should return the updated component
component
)
```

#### BigBang Phases  :before-start :after-start

This keys represents the different phases that your actions could happen.  

All phases recieve a vector of bigbang actions (that are vectors too)

**:before-start** here the actions that need to be applyed at the same invocation time that component/start, but just before component/start  
**:after-start**  here the actions that need to be applyed at the same invocation time that component/start, but just after component/start 


## Example

```clojure

;;  construct your instance of SystemMap as usual
(def system-map (new-system-map))


;; instead of calling component/start call bigbang/expand 
(def system (bigbang/expand system-map
                            {:before-start [[identity/add-meta-key system-map]
                                            [identity/assoc-meta-who-to-deps]]
                             :after-start  [[aop/wrap logging-function-invocation]]}))
```

#### Integrate with stuartsierra/reloaded workflow

Replace your reloaded start function for this one

```clojure
(defn start
  "BigBang starting the current development system."
  []
  (alter-var-root #'system #(bigbang/expand % {:before-start [[identity/add-meta-key %]
                                                              [identity/assoc-meta-who-to-deps]]
                                               :after-start [[aop/wrap visualisation-invocation]]})))
```

## BigBang actions available 

Those libs are supported by BigBang

* [tangrammer/co-dependency](https://github.com/tangrammer/co-dependency) co-dependency facility in stuartsierra/component library (Inverse Dependency Inyection)
* [milesian/identity](https://github.com/milesian/identity) identity actions to apply to components and dependencies 
* [milesian/aop](https://github.com/milesian/aop) facility to apply AOP in stuartsierra/component library

#### Releases and Dependency Information

```clojure
[milesian/bigbang "0.1.1"]
```

```clojure
:dependencies [[org.clojure/clojure "1.6.0"]
               [com.stuartsierra/component "0.2.2"]]
```


## License

Copyright © 2014 Juan Antonio Ruz 

Distributed under the [MIT License](http://opensource.org/licenses/MIT). This means that pieces of this library may be copied into other libraries if they don't wish to have this as an explicit dependency, as long as it is credited within the code.

"Universe Expansion" image [@ Public Domain](http://commons.wikimedia.org/wiki/File:Universe_expansion2.png)
