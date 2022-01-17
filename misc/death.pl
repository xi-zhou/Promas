:- use_module(library(db)).
:- sqlite_load('/Users/z.x/eclipse-workspace-2020-06/JZombies_Demo/misc/test.db').

0.01:: dies(PERSONx) :- in_quarantine(PERSONx).
query(dies(PERSONx)).