% Лабораторная работа №2 по дисциплине "Логические основы %интеллектуальных систем"
% Выполнил студент группы 321701
% Астахов Артём Сергеевич
% Вариант: 5
% Дата изменения: 05.06.2025

% В файле содержится исходный код для решения задачи:
% Пять супружеских пар необходимо переправить на трехместной лодке. Ни одна %супруга не может находиться на берегу или в лодке без супруга.
% Источники: 
% 1. Логические основы интеллектуальных систем. Практикум : учеб.- метод. %пособие / В. В. Голенков [и др.].
% – Минск : БГУИР, 2011. – 70 с. : ил. ISBN 978-985-488-487-5.
% 2. SWI Prolog [Электронный ресурс]. -- Режим доступа https://www.swi-%prolog.org/


man(h1).
man(h2).
man(h3).
man(h4).
man(h5).
women(w1).
women(w2).
women(w3).
women(w4).
women(w5).

add(X,L,[X|L]).

writelist([]).
writelist([Element|RestList]):-
	write(Element), nl,
	writelist(RestList).

start1:-
    travel([left, [h1,w1,h2,w2,h3,w3, h4, w4, h5, w5],[]], [], [right, [], _]).
start2:-
    travel([left, [h1,h2,h3,h4,h5, w1,w2,w3,w4,w5],[]], [], [right, [], _]).
start3:-
    travel([left, [w1,w2,w3,w4,w5,h1,h2,h3,h4,h5],[]], [], [right, [], _]).
start4:-
    travel([left, [w1,w2,h4,h5],[w3,w4,w5,h1,h2,h3]], [], [right, [], _]).
start5:-
    travel([right, [h5],[w1,w2,w3,w4,w5,h1,h2,h3,h4]], [], [right, [], _]).
start6:-
    travel([right, [w1],[w2,w3,w4,w5,h1,h2,h3,h4,h5]], [], [right, [], _]).
start7:-
    travel([left, [w1],[w2,w3,w4,w5,h1,h2,h3,h4,h5]], [], [right, [], _]).
start8:-
    travel([left, [w2,w3,w4,w5,h1,h2,h3,h4,h5],[w1]], [], [right, [], _]).
start9:-
    travel([right, [w1,w2,w3,w4,w5,h1,h2,h3,h4,h5],[w1]], [], [right, [], _]).
start10:-
    travel([right, [], [h1,w1,h2,w2,h3,w3, h4, w4, h5, w5]], [], [right, [], _]).
start11:-
    travel([left, [], [h1,w1,h2,w2,h3,w3, h4, w4, h5, w5]], [], [right, [], _]).

travel([left, [], _],_):-fail.

travel(Goal,PastIterations, Goal):-
	add(Goal, PastIterations, NewPastIterations),
	reverse(NewPastIterations, SortedPastIterations),
	writelist(SortedPastIterations).

travel(CurrentState, PastIterations, Goal):-
    move(CurrentState, NewState),
    add(CurrentState, PastIterations, NewPastIterations),
    travel(NewState,  NewPastIterations, Goal).
move([left, LeftCoast, RightCoast],
     [right, NewLeftCoast2, NewRightCoast]):-
   	validstates([LeftCoast, RightCoast]),
    	length(LeftCoast, 2),
    	member(FirstPerson,  LeftCoast),
	rest(FirstPerson, LeftCoast, RestLeftCoast),
	member(SecondPerson, RestLeftCoast),
	delete(LeftCoast, FirstPerson, NewLeftCoast),
	delete(NewLeftCoast, SecondPerson, NewLeftCoast2),
	append([FirstPerson, SecondPerson], RightCoast, NewRightCoast),
	validstates([NewLeftCoast2, NewRightCoast]).
move([left, LeftCoast, RightCoast],
     [right, NewLeftCoast3, NewRightCoast]):-
    	validstates([LeftCoast, RightCoast]),
	member(FirstPerson,  LeftCoast),
	rest(FirstPerson, LeftCoast, RestLeftCoast),
	member(SecondPerson, RestLeftCoast),
    	rest(SecondPerson, RestLeftCoast, RestLeftCoast3),
	member(ThirdPerson, RestLeftCoast3),
	delete(LeftCoast, FirstPerson, NewLeftCoast),
	delete(NewLeftCoast, SecondPerson, NewLeftCoast2),
    	delete(NewLeftCoast2, ThirdPerson, NewLeftCoast3),
	append([FirstPerson, SecondPerson, ThirdPerson], RightCoast, NewRightCoast),
	validstates([NewLeftCoast3, NewRightCoast]).
move([right, LeftCoast, RightCoast],
     [left, [RightPerson|LeftCoast], NewRightCoast]):-
	member(RightPerson, RightCoast),
    	man(RightPerson),
    	validstates([LeftCoast, RightCoast]),
	delete(RightCoast, RightPerson, NewRightCoast),
	validstates([[RightPerson|LeftCoast], NewRightCoast]).
rest(Element, [Element | RestOfList], RestOfList).
rest(Element, [_ | RestOfList], Result) :-
	rest(Element, RestOfList, Result).
validstates([LeftCoast, RightCoast]):-
	validstate(LeftCoast),
	validstate(RightCoast).
validstate(Persons):-
    	member(X, Persons),
    	man(X).
validstate(Persons):-
   	length(Persons, 0).