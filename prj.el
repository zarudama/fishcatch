(jde-project-file-version "1.0")
(jde-set-variables

 '(jde-jdk (quote ("1.7")))
 '(jde-jdk-registry (quote (
                            ;;("1.7" . "c:/Program Files/Java/jdk1.7.0_09/")
                            ("1.7" . "/usr/lib/jvm/jdk1.7.0/")
                            )))
 '(jde-jdk-doc-url "http://docs.oracle.com/javase/jp/6/api/") ;; JDKのjavadocのURL
 '(jde-help-docsets '(("JDK API" "http://docs.oracle.com/javase/jp/6/api/" nil)
                      ("libGDX API" "http://libgdx.badlogicgames.com/nightlies/docs/api/" nil)
                      ))
 '(jde-help-use-frames nil)

 )
 (setq jde-import-auto-sort t) ;; import文挿入時に自動でソート


