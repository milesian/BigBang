# BigBang
### compose component/update(s)-system in component/start invocation time

![image](https://dl.dropboxusercontent.com/u/8688858/bigbang.png)


[Why did I write this library?](http://tangrammer.github.io/posts/12-12-2014-bigbang.html)

BigBang generalizes the "how and when can you customize your system?" **letting you compose all your component updates in the same component/start invocation time**, but distinguishing those updates that have to happen just-before from those that have to happen just-after component/start.

So you write this code

```clojure

(bigbang/expand system-map
                        {:before-start [[fn1 arg1]
                                        [fn2 arg1 arg2]]
                         :after-start  [[fn3 arg1 arg2]
                                        [fn4 arg1]]})
```

and you get something similar to: 

```clojure 
(update-system system-map #(comp (apply fn4 [arg1]) (apply fn3 [arg1 arg2]) component/start (apply fn2 [arg1 arg2]) (apply fn1 [arg1]))
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
