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
plot(xas(1:12),old(1:12),'ro')
hold on
plot(xas(1:11),new(1:11),'bo', 'LineWidth', 2)
set(gca,'YScale','log')
[p, ~,mu ] = polyfit(T.Var1,  T.Var2,  11);
[p2,~,mu2] = polyfit(T2.Var1, T2.Var2, 10);
f  = polyval(p,  xas, [], mu);
f2 = polyval(p2, xas, [], mu2);
plot(xas,f, 'r--')
plot(xas,f2, 'b--', 'LineWidth',  2)
ylabel('nanoseconden')
xlabel('comparator')
legend('old', 'new', 'benadering old', 'benadering new', 'Location', 'northwest')

%%
benadering_uur = f2(12) / (1000 * 60 * 60)
benadering_dag = benadering_uur /24