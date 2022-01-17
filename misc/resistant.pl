:- use_module(library(db)).
:- sqlite_load('/Users/z.x/eclipse-workspace-2020-06/JZombies_Demo/misc/test.db').

resistant(PERSONx) :- is_resistant(PERSONx), \+reinfected(PERSONx).
resistant(PERSONx) :- recovers(PERSONx);vaccinated(PERSONx).
0.02 ::  recovers(PERSONx) :- is_ill(PERSONx).
0.05 :: recovers(PERSONx) :- in_quarantine(PERSONx).
0.15 :: vaccinated(PERSONx) :- is_cautious(PERSONx).
0.1::  vaccinated(PERSONx) :- is_social(PERSONx).
query(resistant(PERSONx)).
