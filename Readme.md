Короче так.
Вот приложение:
https://drive.google.com/file/d/1MhbD8h9-BOodQnJIP1W9va44VP2bbAQd/view?usp=sharing

Вот данные на сервере:
https://anmedia-server.000webhostapp.com/Data

Весь код если интересно:
https://github.com/Undrown/AnMediaHours
там в корне папка httpserver - там сервер на php

Вход из приложения по номеру телефона. пока забиты нашару номера телефонов 111, 333, 444
Входишь по номеру и номер сохраняется на устройстве.
Данные на сервере хранятся в стандартной форме:
 - длинное число (количество миллисекунд прошедших с начала эпохи) - это дата со временем
 - uid - это UserID - Int
 - comment - обычный String<br>
TODO:
 - сделать чтобы юзеру показывалась выборка его часов, с возможностью настроить промежуток времени
 - сделать вебморду/админку чтобы из браузера просматривать данные, сводки, диаграммы
 - логика подсчёта часов и денег на сервере не реализована вообще
 - хранить данные в приложении при отсутствии интернета и отправлять пакеты на сервер при подключениии к инету
