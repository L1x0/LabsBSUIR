import tkinter as tk
from math import floor

cell = 10

root = tk.Tk()
root.title("Line Algorithms")

alg = tk.StringVar(value="DDA")
debug = tk.BooleanVar(value=False)

frame = tk.Frame(root)
frame.pack(fill="both", expand=True)

tools = tk.Frame(frame, width=120)
tools.pack(side="left", fill="y")

c = tk.Canvas(frame, width=600, height=600, bg="white")
c.pack(side="right")

points = []
drawn = []
step = 0
p = []

def grid():
    for i in range(0,600,cell):
        c.create_line(i,0,i,600,fill="#eee")
        c.create_line(0,i,600,i,fill="#eee")

def gray(i):
    v = int(255*(1-i))
    return f"#{v:02x}{v:02x}{v:02x}"

def draw_pix(x,y,col="black"):
    return c.create_rectangle(
        x*cell,y*cell,(x+1)*cell,(y+1)*cell,
        fill=col,outline=""
    )

def next_step():
    global step
    if step < len(points):
        p = points[step]

        if len(p)==3:
            x,y,i=p
            drawn.append(draw_pix(x,y,gray(i)))
        else:
            x,y=p
            drawn.append(draw_pix(x,y))

        step+=1

def prev_step():
    global step
    if step>0:
        step-=1
        c.delete(drawn.pop())

def dda(x0,y0,x1,y1):
    pts=[]
    dx,dy=x1-x0,y1-y0
    n=max(abs(dx),abs(dy))
    x,y=x0,y0

    for _ in range(n+1):
        pts.append((round(x),round(y)))
        x+=dx/n
        y+=dy/n

    return pts

def bres(x0,y0,x1,y1):
    pts=[]

    dx,dy=abs(x1-x0),abs(y1-y0)
    sx=1 if x0<x1 else -1
    sy=1 if y0<y1 else -1
    err=dx-dy

    while True:
        pts.append((x0,y0))
        if x0==x1 and y0==y1:
            break

        e2=2*err

        if e2>-dy:
            err-=dy
            x0+=sx

        if e2<dx:
            err+=dx
            y0+=sy

    return pts

def wu(x0,y0,x1,y1):
    pts=[]

    steep = abs(y1-y0) > abs(x1-x0)

    if steep:
        x0,y0,x1,y1 = y0,x0,y1,x1

    if x0>x1:
        x0,x1,y0,y1 = x1,x0,y1,y0

    dx=x1-x0
    dy=y1-y0
    k=dy/dx if dx else 0

    y=y0

    for x in range(x0,x1+1):

        f=y-floor(y)

        if steep:
            pts.append((floor(y),x,1-f))
            pts.append((floor(y)+1,x,f))
        else:
            pts.append((x,floor(y),1-f))
            pts.append((x,floor(y)+1,f))

        y+=k

    return pts

def click(e):
    global p,points,step,drawn

    p.append((e.x//cell,e.y//cell))

    if len(p)==2:

        x0,y0=p[0]
        x1,y1=p[1]

        points={
            "DDA":dda,
            "Bresenham":bres,
            "Wu":wu
        }[alg.get()](x0,y0,x1,y1)

        step=0
        drawn=[]

        if not debug.get():

            for pt in points:

                if len(pt)==3:
                    x,y,i=pt
                    draw_pix(x,y,gray(i))
                else:
                    x,y=pt
                    draw_pix(x,y)

        p=[]

c.bind("<Button-1>",click)

m=tk.Menu(root)
root.config(menu=m)

algm=tk.Menu(m,tearoff=0)

for a in ["DDA","Bresenham","Wu"]:
    algm.add_radiobutton(label=a,variable=alg,value=a)

m.add_cascade(label="Отрезки",menu=algm)

mode=tk.Menu(m,tearoff=0)
mode.add_checkbutton(label="Отладочный режим",variable=debug)
m.add_cascade(label="Режим",menu=mode)

tk.Label(tools,text="Алгоритм").pack(pady=5)

for a in ["DDA","Bresenham","Wu"]:
    tk.Radiobutton(tools,text=a,variable=alg,value=a).pack(anchor="w")

tk.Checkbutton(tools,text="Debug",variable=debug).pack(pady=10)

tk.Button(tools,text="Назад",command=prev_step).pack(fill="x")
tk.Button(tools,text="Дальше",command=next_step).pack(fill="x")

grid()

root.mainloop()