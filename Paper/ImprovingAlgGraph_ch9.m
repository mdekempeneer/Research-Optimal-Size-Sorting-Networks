close all
clc

X = 2:25;
Y2 = zeros([1 24]);
Y4 = zeros([1 24]);
Y5 = zeros([1 24]);
Y6 = zeros([1 24]);

% Y1 - 4,2u
Y1       = [10, 7, 16, 34, 45, 103, 245, 1669, 15624, 154239, 1062174, 3797288, 5661054, 3404493, 898401, 126276, 12442, 976, 68, 10, 3, 1, 0, 0];
% Y2 - 'Job20281461 - 256' - del/add 22 comps
Y2(1:21) = [9,  5, 12, 21, 33, 178, 625, 2292, 17111, 141878, 966757,  3337937, 4851132, 2699099, 717380, 108455, 10866, 886, 61, 15, 3];
% Y3 - curr Fastest: 3.5u - del/add
Y3       = [8,  5, 12, 20, 33, 173, 605, 2228, 17128, 139224, 945710,  3287401, 4705779, 2692819, 709728, 103660, 10027, 844, 59, 13, 3, 0, 1, 0];
% Y4 - cancelled - noTrim - 'Job20281806 - noTrim - 256'
Y4(1:11)  = [9,  6, 12, 20, 32, 173, 609, 2494, 27697, 238346, 1408245];
% Y5 - :  - pruneFrontOnly - Job20283061 - pruneCut - 256
Y5        = [9,  5, 12, 20, 31, 173, 599, 2380, 13088, 124332, 922677, 3345395, 4873226, 2967767, 821732, 123315, 12223, 908, 68, 13, 3, 0, 0, 0];  
% Y6 - nullJump - Job20290089 - 256
Y6(1:12)  = [9,  5, 13, 19, 33, 167, 582, 2166, 11700, 117072, 889036, 3202815];

T = [Y1 ; Y3 ; Y6];
figure
bar(X, T')
title('Running time ms')
set(gca,'YScale','log')
legend('Vorige', 'Job20281753 - 256', 'Job20290089 - nullJump - 256')

M = zeros([1 24]);
N = zeros([1 24]);
O = zeros([1 24]);
M(1:21) = (Y1(1:21) - Y2(1:21))/1000;
N       = (Y1 - Y3)/1000;
O       = (Y3 - Y6)/1000;
figure
bar(X, [M ; N ; O]')
set(gca,'YScale','log')
title('Running time difference in s')
legend('1 tov 2', '1 tov 3', '3 tov 6');

% Diff acc
AccM = cumsum(M);
AccN = cumsum(N);
AccO = cumsum(O);
figure
bar(X, [AccM ; AccN ; AccO]')
set(gca, 'YScale', 'log')
title('Acc diff run time in s')
legend('1 tov 2', '1 tov 3', '3 tov 6');

% Acc 
Acc1 = cumsum(Y1/1000);
Acc3 = cumsum(Y3/1000);
Acc6 = cumsum(Y6/1000);
figure
bar(X, [Acc1 ; Acc3 ; Acc6]')
set(gca,'YScale','log')
title('Acc run time in s')
legend('Acc 1', 'Acc 3', 'Acc 6');


DiffAcc3 = Acc1 - Acc3;
DiffAcc6 = Acc3 - Acc6;
figure
bar(X, [DiffAcc3 ; DiffAcc6]')
set(gca,'YScale','log')
title('Acc run time difference in s')
legend('1 tov 3', '3 tov 6');


