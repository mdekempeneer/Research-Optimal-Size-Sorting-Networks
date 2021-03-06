clc
clear all

% waarden

% hours
       %a2 =   1.395e+19; %  (-2.219e+19, 5.009e+19)
       %b2 =       40.15; %(38.39, 41.9)
       %c2 =       4.331; % (4.193, 4.469)
a1 =   1.395e+19;
b1 =       40.15;
c1 =       4.193;

%a1 =   1.395e+19;
%b1 =       40.15;
%c1 =       4.203;

x_b10 = (2:16);
f = @(x) a1*exp(-((x-b1)/c1).^2);

plot(x_b10,f(x_b10), 'o-')
set(gca,'YScale','log')
ylabel('uur')
xlabel('comparator')

%%
x_b10_2 = (2:16);

% days 10
a2 =   3.559e+05;  %(-2.165e+06, 2.878e+06)
b2 =       21.05;  %(16.24, 25.86)
c2 =       2.409;  %(1.731, 3.087)

       %a2 =   9.089e+04  %(-5.204e+05, 7.022e+05)
       %b2 =       20.12  %(15.54, 24.7)
       %c2 =       2.274  %(1.59, 2.958)

%a2 =        7744  %(-3.848e+04, 5.397e+04)
%b2 =       18.44  %(14.36, 22.52)
%c2 =       2.008  %(1.319, 2.697)

f2 = @(x) a2*exp(-((x-b2)/c2).^2);

% days 9
       a3 =      0.0532  %(0.05281, 0.05358)
       b3 =       13.91  %(13.9, 13.92)
       c3 =       1.509  %(1.497, 1.522)
f3 = @(x) a3*exp(-((x-b3)/c3).^2);

figure
plot(x_b10_2,f2(x_b10_2), 'b*-')
set(gca,'YScale','log')
ylabel('Uitvoeringstijd (dagen)')
xlabel('Comparator')
hold on
grid on
plot((2:25), f3((2:25)), 'r.-');
legend('Benadering 10 kanalen, t.e.m. comparator 16', 'Model 9 kanalen.');
