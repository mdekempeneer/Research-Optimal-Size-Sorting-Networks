clc
clear all

% waarden
a1 = 1.386e+19;
b1 = 39.14;
c1 = 4.331;

x_b10 = (2:16);
y_b10 = a1*exp(-((x_b10-b1)/c1).^2);

plot(x_b10,y_b10)
set(gca,'YScale','log')
ylabel('uur')
xlabel('comparator')