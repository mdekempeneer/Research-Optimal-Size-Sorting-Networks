close all
clc

X = 2:25;
m = 11;
Y2 = zeros([1 24]);
Y3 = zeros([1 24]);
Y4 = zeros([1 24]);
Y1       = [10, 7, 16, 34, 45, 103, 245, 1669, 15624, 154239, 1062174, 3797288, 5661054, 3404493, 898401, 126276, 12442, 976, 68, 10, 3, 1, 0, 0];
Y2(1:21) = [9,  5, 12, 21, 33, 178, 625, 2292, 17111, 141878, 966757,  3337937, 4851132, 2699099, 717380, 108455, 10866, 886, 61, 15, 3];
Y3       = [8,  5, 12, 20, 33, 173, 605, 2228, 17128, 139224, 945710,  3287401, 4705779, 2692819, 709728, 103660, 10027, 844, 59, 13, 3, 0, 1, 0];
Y4(1:m)  = [9,  6, 12, 20, 32, 173, 609, 2494, 27697, 238346, 1408245];

T = [Y1 ; Y2; Y3; Y4];
figure
bar(X, T')
title('Running time')
set(gca,'YScale','log')
legend('Vorige', 'Job20281461 - 256', 'Job20281753 - 256', 'Job20281806 - noTrim - 256')


M = zeros([1 24]);
N = zeros([1 24]);
M(1:21) = (Y1(1:21) - Y2(1:21))/1000;
N       = (Y1 - Y3)/1000;
figure
bar(X, [M ; N]')
set(gca,'YScale','log')
title('Running time difference in s')

% Diff acc
AccM = cumsum(M);
AccN = cumsum(N);
figure
bar(X, [AccM ; AccN]')
set(gca, 'YScale', 'log')
title('Acc diff run time in s')

% Acc 
Acc1 = cumsum(Y1/1000);
Acc2 = cumsum(Y2/1000);
Acc3 = cumsum(Y3/1000);
figure
bar(X, [Acc1 ; Acc2; Acc3]')
set(gca,'YScale','log')
title('Acc run time in s')


DiffAcc1 = Acc1 - Acc2;
DiffAcc2 = Acc1 - Acc3;
figure
bar(X, [DiffAcc1 ; DiffAcc2]')
set(gca,'YScale','log')
title('Acc run time difference in s')


