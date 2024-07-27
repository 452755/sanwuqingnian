from tkinter import *
import requests
import jsonpath
import os
from urllib.request import urlretrieve


def song(url, title):
    os.makedirs('music', exist_ok=True)
    path ='music\{}.mp3'.format(title)
    text.insert(END, '歌曲正在下载：{}.mp3'.format(title))
    text.see(END)
    text.update()
    urlretrieve(url, path)
    text.insert(END, '下载完毕：{}，请试听'.format(title))


def get_music_name():
    name = entry.get()
    pingtai = var.get()
    url = 'http://www.youtap.xin'
    params = {
        'input': name,
        'filter': 'name',
        'type': pingtai,
        'page': 1,
    }
    headers = {
        'X-Requested-With': 'XMLHttpRequest'
    }
    resp = requests.post(url, data=params, headers=headers)
    data = resp.json()
    title = jsonpath.jsonpath(data, '$..title')[0]
    author = jsonpath.jsonpath(data, '$..author')[0]
    url = jsonpath.jsonpath(data, '$..url')[0]
    print(title)
    print(author)
    print(url)
    song(url, title)



# 1.创建画布
root = Tk()
# 2.添加标题
root.title('音乐下载器')
# 3.设置窗口大小
root.geometry('570x470+500+200')
# 4.标签
label = Label(root, text='请输入下载的歌曲：', font=('华文楷体', 20))
# 5.定位
label.grid()
# 6.输入框
entry = Entry(root, font=('隶书', 20))
entry.grid(row=0, column=1)
# 单选按钮
var = StringVar()
r1 = Radiobutton(root, text='网易云', variable=var, value='netease', font=('华文楷体', 15))
r1.grid(row=1, column=0)

r2 = Radiobutton(root, text='QQ', variable=var, value='qq', font=('华文楷体', 15))
r2.grid(row=1, column=1)

r3 = Radiobutton(root, text='酷狗', variable=var, value='kugou', font=('华文楷体', 15))
r3.grid(row=2, column=1)

# 7.列表框
text = Listbox(root, font=('隶书', 16), width=50, heigh=14)
text.grid(row=3, columnspan=2)
# 8.下载按钮
btn1 = Button(root, text='开始下载', font=('隶书', 15), command=get_music_name)
btn1.grid(row=4, column=0)
btn2 = Button(root, text='退出程序', font=('隶书', 15), command=root.quit)
btn2.grid(row=4, column=1)
# 显示画布
root.mainloop()
