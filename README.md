# BigBang

Expands the universe behind stuartsierra **component/update-system** function to [customize](https://github.com/stuartsierra/component#customization) the way your system starts.
 
![image](https://dl.dropboxusercontent.com/u/8688858/bigbang.png)

### Did you ever wonder how your system started? 

As you can check, every time you call [component/start-system](https://github.com/stuartsierra/component/blob/master/src/com/stuartsierra/component.clj#L151) you are indirectly using ```component/update-system``` fn.   
So let's take a look to this main fn:

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

**Nothing strange at this side!**,we can see a very normal reduce function. Then, behind (component/start-system) call there is a system reduction using an update-component-with-fresh-injected-dependencies-fn. 
And logically, **if we pass a system we get a new-updated-system**.

# The only way to customize your system (by chance!) is update-system 
Also you can find in component/[README](https://github.com/stuartsierra/component/blob/master/README.md#customization) the relevance of update-system fn as **system customization way**:

> Both ```update-system``` and ```update-system-reverse``` take a function as an argument   
and call it on each component in the system. 
Along the way, they ```assoc``` in the updated dependencies of each component.   
The ```update-system``` function iterates over the components in dependency order   
 (a component will be called after its dependencies)....


## But wait!! have you already read the component definition?
Also extracted from the component/[README](https://github.com/stuartsierra/component/blob/master/README.md)
> For the purposes of this framework,    
> a component is a collection of functions or procedures which   
> **share some runtime state**.

**I'd like to highlight here that this share runtime state (or runtime value) is produced at the invocation time of  ```(update-system system component/start)```** 

# Joining new-updated-states with share-runtime-state   

Every update-system reduction returns a new-updated-system (meaning a new state or a new value) but our components need to share the **"last"** new updated state to be updated.

Or in other words, try to find the differences of following two sequence calls:

```clojure
(-> system 
    (component/update-system your-fn your-args)  ;;  new-updated-system 
    (component/update-system component/start)    ;;  started-new-updated-system)
(-> system 
    (component/update-system component/start)    ;;  started-system 
    (component/update-system your-fn your-args)  ;;  new-updated-started-system)
```

In first sequence, your components will start (or will share runtime state) using previous new-updated-system. But in second sequence, your component-share-runtime-state will not use the subsequent update, due that started-system is a previous runtime state from new-updated-started-system

In other words:   
**When we call start we get the running system and further updates over this state are not used in ** 


**Hey!, you don't really need BigBang library to work with stuartsierra/component**

That's true, but if you try to apply several transformations (or you can say reductions) to your system, being able to specify those that have to be invoked  **just before same start-invocation-time** from  those that have to be invoked  **just after same start-invocation-time**,  then BigBang library it's great for it! 

#### Releases and Dependency Information

```clojure
[milesian/bigbang "0.1.1"]
```

```clojure
:dependencies [[org.clojure/clojure "1.6.0"]
               [com.stuartsierra/component "0.2.2"]]
```


##  BigBang Actions and Phases
An action is specified in a very similar way as you use [clojure.core/apply](http://clojuredocs.org/clojure.core/apply) but without using "apply" and enclosing it with brackets 
```clojure
[action-function action-arg0 action-arg1 action-arg2 ...]
```
Actions must at least receive the component instance to update (and anymore args ) and has to return the component updated
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


##  BigBang/expand

```bigbang/expand``` needs a common system-map instance and a map with 2 keys ```:before-start :after-start``` 

```clojure 
(defn expand
  [system-map {:keys [before-start after-start]}]
...)

```


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



## License

Copyright © 2014 Juan Antonio Ruz 

Distributed under the [MIT License](http://opensource.org/licenses/MIT). This means that pieces of this library may be copied into other libraries if they don't wish to have this as an explicit dependency, as long as it is credited within the code.

"Universe Expansion" image [@ Public Domain](http://commons.wikimedia.org/wiki/File:Universe_expansion2.png)
