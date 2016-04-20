close all
clc

X = [4,5,6,7,8,9];
Y1 = [16.93, 25.05, 82.44, 141.48, 6680.31, 15135358.01];
% Y2- 'Job20281418 - 8ch',
Y2 = [17.33, 24.48, 65.48, 258.47, 6663.35, 0];
% Y3- 'Job20281449 - 1024', 'Job20281461 - 256'
Y3 = [19.44, 24.28, 70.03, 343.58, 14770.5, 0];
% Y4- 'Job20281461 - 256'
Y4 = [20.62, 24.06, 67.50, 259.51, 7785.14, 12854614.30];
Y5 = [10.09, 25.75, 66.82, 254.44, 6797.80, 12615521.41]; % improv delete/add
% Y6- 'Job20281806 - noTrim - 256'
Y6 = [11.39, 25.03, 68.52, 254.52, 9434.95, 0]; % No trim
Y7 = [22.82, 24.77, 65.81, 252.66, 6104.70, 13208016.36]; % prune. cut only check front
T = [Y1 ; Y5 ; Y7];


figure
bar(X, T')
set(gca,'YScale','log')
title('runtime ms');
legend('Vorige', 'Job20281753 - 256', 'Job20283061 - pruneCut - 256','Location','northwest')

figure
X5 = (Y1 - Y5)/1000;
X7 = (Y5 - Y7)/1000;
bar(X, [X5 ; X7]');
set(gca, 'YScale', 'log')
title('runtime diff in s');
legend('1 - 5', '5 - 7', 'Location', 'northwest');

