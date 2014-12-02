# BigBang

Expands the universe behind  [com.stuartsierra.component/update-system](https://github.com/stuartsierra/component/blob/master/src/com/stuartsierra/component.clj#L117) functionality 
 
![image](https://dl.dropboxusercontent.com/u/8688858/bigbang.png)


### Assumptions

Working with stuartsierra/component library enforces us to use component/update-system function ([indirectly](https://github.com/stuartsierra/component/blob/master/src/com/stuartsierra/component.clj#L143-L151) or directly) for starting our system.    

**Here the great documentation of this fn**
```clojure 
  "Invokes (apply f component args) on each of the components at
  component-keys in the system, in dependency order. Before invoking
  f, assoc's updated dependencies of the component."
```

#### Releases and Dependency Information


```clojure
[milesian/bigbang "0.1.1"]
```

```clojure
:dependencies [[org.clojure/clojure "1.6.0"]
               [com.stuartsierra/component "0.2.2"]]
```


### Ey!, you don't really need BigBang library to work with stuartsierra/component

That's true, but if you try to apply several transformations (or you can called them reductions too) distinguishing those that must be done at the same invocation time of component/start (being able to specify also those that just before same start-invocation or just after same start-invocation) from those that can happen before or after system is started, then BigBang library it's great for you! 




##  bigbang/expand DSL

As you can see ```bigbang/expand``` needs a common system-map instance  and a map with 3 keys ```:before-start :on-start :after-start``` 

```clojure 
(defn expand
  [system-map {:keys [before-start on-start after-start]}]
...)

```

#### BigBang Phases  :before-start :on-start :after-start

This keys represents the different phases that your actions could happen.  

All phases recieve a vector of vectors

**:before-start** in this place your actions don't need the components to be initialized   
**:on-start** in this place your actions need to be applyed at the same invocation time that component/start fn   
**:after-start** in this place your actions need to be applyed once your components have been initialized 




#### BigBang Actions

Actions have this format ```[action-function action-arg0 action-arg1 action-arg2 ...]```



## Example

```clojure

;;  construct your instance of SystemMap as usual
(def system-map (new-system-map))


;; instead of calling component/start call bigbang/expand 
(def system (bigbang/expand system-map {:before-start [[identity-actions/add-meta-key system-map]]
                                        :on-start [[identity-actions/assoc-meta-who-to-deps]
                                                   [component/start]
                                                   [aop-actions/wrap improved-logging]]
                                        :after-start []}))

```


## BigBang Actions, available libraries 

Those libs are supported by BigBang

* [milesian/identity](https://github.com/milesian/identity) identity actions to apply to components and dependencies 
* [tangrammer/co-dependency](https://github.com/tangrammer/co-dependency) co-dependency facility in stuartsierra/component library (Inverse Dependency Inyection)
* [milesian/aop](https://github.com/milesian/aop) facility to apply AOP in stuartsierra/component library



## License

Copyright © 2014 Juan Antonio Ruz 

Distributed under the [MIT License](http://opensource.org/licenses/MIT). This means that pieces of this library may be copied into other libraries if they don't wish to have this as an explicit dependency, as long as it is credited within the code.

"Universe Expansion" image [@ Public Domain](http://commons.wikimedia.org/wiki/File:Universe_expansion2.png)
