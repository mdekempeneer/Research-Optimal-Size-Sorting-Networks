
% ParseStatements:
%   This predicate will create a list of bigger and
%   smaller channels, based on the given channel.
%   Implemented with tail recursion.

% Base case when all statements are checked.
parseStatements(_, [], Below, Above, Below, Above) :- !.

% ParseStatements in the list when the next one is smaller(A,B).
parseStatements(Channel, [smaller(A,B)|Tail], Below, Above, FinalBelow, FinalAbove) :-
    (   Channel == A
    ->  parseStatements(Channel, Tail, Below, [B|Above], FinalBelow, FinalAbove)
    ; (   Channel == B
      ->  parseStatements(Channel, Tail, [A|Below], Above, FinalBelow, FinalAbove)
      ;   parseStatements(Channel, Tail, Below, Above, FinalBelow, FinalAbove)
      )
    ).

% ParseStatements in the list when the next one is bigger(A,B).
parseStatements(Channel, [bigger(A,B)|Tail], Below, Above, FinalBelow, FinalAbove) :-
    (   Channel == A
    ->  parseStatements(Channel, Tail, [B|Below], Above, FinalBelow, FinalAbove)
    ; (   Channel == B
      ->  parseStatements(Channel, Tail, Below, [A|Above], FinalBelow, FinalAbove)
      ;   parseStatements(Channel, Tail, Below, Above, FinalBelow, FinalAbove)
      )
    ).

% Predicate that can be used as interface for parseStatements with tail recursion.
parseStatements(Channel, List, FinalSet) :-
    parseStatements(Channel, List, [], [], Below, Above),
	  findall(smaller(A,B), (member(A,Below),member(B,Above)), Temp),
    append(List, Temp, FinalList),
    list_to_set(FinalList, FinalSet).


% ChannelsSet:
%   This predicate will check for all channels that are compared within
%   a list of comparisons. It uses tail recursion, so a more readable
%   interface is provided.

% Base case to fetch list of channels from comparisons list.
channelsList([], List, List).

% Fetch channels when the first term is smaller.
channelsList([smaller(A,B)|Tail], CurrentList, FinalList) :-
    channelsList(Tail, [A,B|CurrentList], FinalList).

% Fetch channels when the first term is bigger.
channelsList([bigger(A,B)|Tail], CurrentList, FinalList) :-
    channelsList(Tail, [A,B|CurrentList], FinalList).

% Convert the channels list to a set.
channelsSet(List, Set) :-
    channelsList(List, [], FinalList),
    list_to_set(FinalList, Set),
    !.


% ValidElementForListWithAtoms:
%   Element should be a term (smaller or bigger) that exists when a List
%   is expanded based on a given channel.
validElementForListWithAtoms(Element, Atoms, List) :-
    member(Atom, Atoms),
    parseStatements(Atom, List, FinalList),
    member(Element, FinalList).


% FindStatements:
%   Predicate that is used to expand the given knowledge list
%   consisting of logical comparators of channels. This is the predicate
%   that runs it all!
findStatements(List, Complete) :-
    channelsSet(List, Set),
    findall(Es, validElementForListWithAtoms(Es, Set, List), AllStatements),
    list_to_set(AllStatements, Complete).


% ParseTerms:
%   Predicate is used to recursively convert strings to
%   Terms to work with.

% Base case when all strings are formatted.
parseTerms([], Terms, Terms).

% Parse strings to terms with tail recursion.
parseTerms([Head|Tail], Terms, FinalTerms) :-
  term_string(Term, Head),
  parseTerms(Tail, [Term|Terms], FinalTerms).

% Interface predicate to call parseTerms.
parseTerms(List, Found) :-
  parseTerms(List, [], Found).


% Setup Main function and set verbose mode to normal
:- set_prolog_flag(verbose, normal).
:- initialization main.

% Main function
main :-
    current_prolog_flag(argv, Argv),
    parseTerms(Argv, List),
    findStatements(List, Result),
    print(Result),
    format('~n'),
    halt.
main :-
    halt(1).
