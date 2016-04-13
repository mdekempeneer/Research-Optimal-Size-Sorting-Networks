close all

X = 2:25
n = 21
Y2 = zeros([1 24])
Y1 = [10, 7, 16, 34, 45, 103, 245, 1669, 15624, 154239, 1062174, 3797288, 5661054, 3404493, 898401, 126276, 12442, 976, 68, 10, 3, 1, 0, 0]
Y2(1:n) = [9, 5, 12, 21, 33, 178, 625, 2292, 17111, 141878, 966757, 3337937, 4851132, 2699099, 717380, 108455, 10866, 886, 61, 15, 3]


T = [Y1 ; Y2]
figure
bar(X, T')
title('Running time')
set(gca,'YScale','log')
legend('Vorige', 'Job20281461 - 256')


M = zeros([1 24])
M(1:n) = (Y1(1:n) - Y2(1:n))/1000
figure
bar(X, M')
set(gca,'YScale','log')
title('Running time difference in s')

% Acc diff
Acc1 = cumsum(Y1/1000)
Acc2 = cumsum(Y2/1000)
figure
bar(X, [Acc1 ; Acc2]')
set(gca,'YScale','log')
title('Acc run time in s')


DiffAcc = Acc1 - Acc2
figure
bar(X, DiffAcc')
set(gca,'YScale','log')
title('Acc run time difference in s')


