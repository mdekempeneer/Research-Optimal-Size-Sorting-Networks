X = 2:25
Y2 = zeros([1 24])
Y1 = [10, 7, 16, 34, 45, 103, 245, 1669, 15624, 154239, 1062174, 3797288, 5661054, 3404493, 898401, 126276, 12442, 976, 68, 10, 3, 1, 0, 0]
Y2(1:11) = [9, 5, 12, 21, 33, 178, 625, 2292, 17111, 141878, 966757]

T = [Y1 ; Y2]



bar(X, T')
set(gca,'YScale','log')
legend('Vorige', 'Job20281461 - 256')