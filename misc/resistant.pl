:- use_module(library(db)).
:- sqlite_load('./test.db').

resistant(PERSONx) :- is_resistant(PERSONx), \+reinfected(PERSONx).
resistant(PERSONx) :- recovers(PERSONx);vaccinated(PERSONx).
0.03 ::  recovers(PERSONx) :- is_ill(PERSONx).
0.08 :: recovers(PERSONx) :- in_quarantine(PERSONx).
0.05 :: vaccinated(PERSONx) :- is_cautious(PERSONx).
0.03::  vaccinated(PERSONx) :- is_social(PERSONx).
query(resistant(PERSONx)).
