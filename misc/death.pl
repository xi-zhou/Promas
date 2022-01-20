:- use_module(library(db)).
:- sqlite_load('./test.db').

0.05:: dies(PERSONx) :- in_quarantine(PERSONx).
query(dies(PERSONx)).
