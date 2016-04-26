clear all
clc
Xas = (5:9)'
Yas =  [  25331106
          61339344
          245313463
          5804849367
          12366987991024]

% variable = 4:0.1:10
% 
% plot(Xas, Yas)
% set(gca,'YScale','log')

T = table(Xas, Yas)
figure
plot(Xas,Yas,'o')
set(gca,'YScale','log')
[p,~,mu] = polyfit(T.Xas, T.Yas, 10);
newXas = 5:11;
f = polyval(p,newXas,[],mu);
hold on
plot(newXas,f)

evaluation = [Yas(2) / Yas(1)
                Yas(3) / Yas(2)
                Yas(4) / Yas(3)
                Yas(5) / Yas(4)];

figure
plot(6:9, evaluation)
set(gca, 'Yscale', 'log')

figure
evaluation2 = [evaluation(2) / evaluation(1)
               evaluation(3) / evaluation(2)
               evaluation(4) / evaluation(3)];
some = (7:9)'
plot(some, evaluation2, 'o')
set(gca, 'Yscale', 'log')
T2 = table(some, evaluation2)
[p2,~,mu2] = polyfit(T2.some, T2.evaluation2, 1);
f2 = polyval(p2,7:10,[],mu2);
hold on
plot(7:10,f2)

f2(4) * evaluation(4) * Yas(5)
f2(4) * evaluation(4) * Yas(5) / (1000000000 * 60 * 60 * 24)