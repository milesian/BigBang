# BigBang

Expands the universe behind  [com.stuartsierra.component/update-system](https://github.com/stuartsierra/component/blob/master/src/com/stuartsierra/component.clj#L117) functionality 
 
![image](https://dl.dropboxusercontent.com/u/8688858/bigbang.png)

Working with stuartsierra/component library enforces us to use **component/update-system** function ([indirectly](https://github.com/stuartsierra/component/blob/master/src/com/stuartsierra/component.clj#L143-L151) or directly) for starting (updating with component/start) our system.    

but... what does update-system actually do?
```clojure 
  "Invokes (apply f component args) on each of the components at
  component-keys in the system, in dependency order. Before invoking
  f, assoc's updated dependencies of the component."
```

BigBang helps you [customize](https://github.com/stuartsierra/component#customization) the way your system starts providing a very simple way. 

In this library you'll find only one function thought to be used as system actions hub.


**Hey!, you don't really need BigBang library to work with stuartsierra/component**

That's true, but if you try to apply several transformations (or you can say reductions) to your system, distinguishing "before" or "after" sequence into the invocation time of component/start (I mean being able to specify those that have to be invoked  just before same start-invocation or just after same start-invocation)  then BigBang library it's great for you! 

##  BigBang Actions and Times
An action is specified very similar as you'll write using [clojure.core/apply](http://clojuredocs.org/clojure.core/apply) but without using "apply" and enclosing it with brackets 
```clojure
[action-function action-arg0 action-arg1 action-arg2 ...]
```
Actions must at least receive the component instance to update (and anymore args ) and has to return the component updated
```
(defn your-action [component & more]
....
;;=> actions should return the updated component
component
)
```
##  BigBang/expand

```bigbang/expand``` needs a common system-map instance and a map with 2 keys ```:before-start :after-start``` 

```clojure 
(defn expand
  [system-map {:keys [before-start after-start]}]
...)

```

#### BigBang Phases  :before-start :after-start

This keys represents the different phases that your actions could happen.  

All phases recieve a vector of vectors

**:before-start** in this place your actions need to be applyed at the same invocation time that component/start, but just before component/start  
**:after-start** in this place your actions need to be applyed at the same invocation time that component/start, but just after component/start 


#### BigBang Actions

Actions have this format ```[action-function action-arg0 action-arg1 action-arg2 ...]```

#### Releases and Dependency Information

```clojure
[milesian/bigbang "0.1.1"]
```

```clojure
:dependencies [[org.clojure/clojure "1.6.0"]
               [com.stuartsierra/component "0.2.2"]]
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


## BigBang Actions, available libraries 

Those libs are supported by BigBang

* [tangrammer/co-dependency](https://github.com/tangrammer/co-dependency) co-dependency facility in stuartsierra/component library (Inverse Dependency Inyection)
* [milesian/identity](https://github.com/milesian/identity) identity actions to apply to components and dependencies 
* [milesian/aop](https://github.com/milesian/aop) facility to apply AOP in stuartsierra/component library



## License

Copyright © 2014 Juan Antonio Ruz 

Distributed under the [MIT License](http://opensource.org/licenses/MIT). This means that pieces of this library may be copied into other libraries if they don't wish to have this as an explicit dependency, as long as it is credited within the code.

"Universe Expansion" image [@ Public Domain](http://commons.wikimedia.org/wiki/File:Universe_expansion2.png)
