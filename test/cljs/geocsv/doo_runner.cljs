(ns geocsv.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [geocsv.core-test]))

(doo-tests 'geocsv.core-test)

