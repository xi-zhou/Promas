:- use_module(library(db)).
:- sqlite_load('./test.db').

P :: infects(PERSONx,PERSONy) :- point(PERSONx,X,Y),
point(PERSONy,A,B), PERSONx\=PERSONy,
D is sqrt((A-X)^2 + (B-Y)^2),D <10 , D>0,P is min(1,0.5/(D^2)).

P :: reinfects(PERSONx,PERSONy) :- point(PERSONx, X, Y),
point(PERSONy, A, B), PERSONx\=PERSONy,
D is sqrt((A-X)^2 + (B-Y)^2),D <10 , D>0,P is min(1,0.05/(D^2)).

ill(PERSONx):-is_ill(PERSONy),(is_cautious(PERSONx); is_social(PERSONx)),infects(PERSONx,PERSONy).
ill(PERSONx):-is_ill(PERSONx),\+recovers(PERSONx), \+in_quarantine(PERSONx).

ill(PERSONx) :- is_ill(PERSONy), is_resistant(PERSONx),reinfects(PERSONx,PERSONy).

query(ill(PERSONx)).
