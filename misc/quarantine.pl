:- use_module(library(db)).
:- sqlite_load('./test.db').

quarantine(PERSONx) :- in_quarantine(PERSONx),\+dies(PERSONx), \+recovers(PERSONx).
quarantine(PERSONx) :- isolation(PERSONx).
0.08:: isolation(PERSONx) :- is_ill(PERSONx).
query(quarantine(PERSONx)).
