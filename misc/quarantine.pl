:- use_module(library(db)).
:- sqlite_load('/Users/z.x/eclipse-workspace-2020-06/JZombies_Demo/misc/test.db').

quarantine(PERSONx) :- in_quarantine(PERSONx),\+dies(PERSONx), \+recovers(PERSONx).
quarantine(PERSONx) :- isolation(PERSONx).
0.2:: isolation(PERSONx) :- is_ill(PERSONx).
query(quarantine(PERSONx)).