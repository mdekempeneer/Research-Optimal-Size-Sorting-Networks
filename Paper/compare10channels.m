clc
clear all
%%
xas             = 2:29;
old             = zeros([1 28]);
old(1:12)       = [6 13 21 44 102 159 391 3436 56887 1237241 26637970 489689647]; % ms

old_s           = old ./ 1000; % s
old_m           = old_s ./ 60; % min
old_u           = old_m ./ 60; % uur
old_d           = old_u ./ 24; % dag
old_totaal_dag  = sum(old_d);

new             = zeros([1 28]);
new(1:11)       = [26 12 20 42 96 410 1147 4875 54304 1079551 22804439]; % ms
new_s           = new ./ 1000; % s
new_m           = new_s ./ 60; % min
new_u           = new_m ./ 60; % uur
new_d           = new_u ./ 24; % dag
new_totaal_dag  = sum(new_d);

%%
figure
plot(2:13, old(1:12), '*-')
xlabel('comparator')
ylabel('miliseconden')
set(gca, 'Yscale', 'log')
hold on
plot(2:12, new(1:11), 'o-')

%%
figure
plot(2:13, old_u(1:12), '*-')
xlabel('comparator')
ylabel('uren')
set(gca, 'Yscale', 'log')
hold on
plot(2:12, new_u(1:11), 'o-')

%%
diff = (old_m(1:11) - new_m(1:11));
cumdiff = cumsum(diff);

figure
plot(2:12, diff, '*-')
set(gca, 'Yscale', 'log')
title('Difference')
xlabel('comparator')
ylabel('minuten')
hold on
plot(2:12, cumdiff, 'o')

%%
figure
bar(xas, [old_m; new_m]')
set(gca, 'Yscale', 'log')

%%

T  = table(xas(1:12), old(1:12));
T2 = table(xas(1:11), new(1:11));
figure
plot(xas(1:12),old(1:12),'o')
hold on
plot(xas(1:11),new(1:11),'o')
set(gca,'YScale','log')
[p, ~,mu ] = polyfit(T.Var1,  T.Var2,  11);
[p2,~,mu2] = polyfit(T2.Var1, T2.Var2, 10);
f  = polyval(p,  xas, [], mu);
f2 = polyval(p2, xas, [], mu2);
plot(xas,f)
plot(xas,f2)
ylabel('nanoseconden')
xlabel('comparator')
legend('old', 'new', 'benadering old', 'benadering new', 'Location', 'northwest')

%%
benadering_uur = f2(12) / (1000 * 60 * 60)
benadering_dag = benadering_uur /24

%%

T3  = table(xas(1:12), old_d(1:12));
T4 = table(xas(1:11), new_d(1:11));
figure
plot(xas(1:12),old_d(1:12),'o')
hold on
plot(xas(1:11),new_d(1:11),'o')
set(gca,'YScale','log')
[p3,~,mu3] = polyfit(T3.Var1, T3.Var2, 11);
[p4,~,mu4] = polyfit(T4.Var1, T4.Var2, 10);
f3 = polyval(p3, xas(1:15), [], mu3);
f4 = polyval(p4, xas(1:15), [], mu4);
plot(xas(1:15),f3)
plot(xas(1:15),f4)
ylabel('dagen')
xlabel('comparator')
legend('old', 'new', 'benadering old', 'benadering new', 'Location', 'northwest')

%%
old(17:28) = fliplr(old(1:12));

old_s           = old ./ 1000; % s
old_m           = old_s ./ 60; % min
old_u           = old_m ./ 60; % uur
old_d           = old_u ./ 24; % dag

new(18:28) = fliplr(new(1:11));

new_s           = new ./ 1000; % s
new_m           = new_s ./ 60; % min
new_u           = new_m ./ 60; % uur
new_d           = new_u ./ 24; % dag

T5 = table(xas(17:28), old_d(17:28));
T6 = table(xas(18:28), new_d(18:28));
figure
plot(xas(17:28),old_d(17:28),'ro')
hold on
plot(xas(18:28),new_d(18:28),'bo')
plot(xas(1:11),new_d(1:11),'bo')
plot(xas(1:12),old_d(1:12),'ro')
set(gca,'YScale','log')
grid on
[p5,~,mu5] = polyfit(T5.Var1, T5.Var2, 11);
[p6,~,mu6] = polyfit(T6.Var1, T6.Var2, 10);
f5 = polyval(p5, xas, [], mu5);
f6 = polyval(p6, xas, [], mu6);
plot(xas(1:15),f3, 'r')
plot(xas(1:15),f4, 'b')
plot(xas(14:28),f5(14:28), 'r')
plot(xas(14:28),f6(14:28), 'b')
% ylabel('dagen')
% xlabel('comparator')
% legend('old', 'new', 'benadering old', 'benadering new', 'Location', 'northwest')