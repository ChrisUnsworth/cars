from PyQt5.QtCore import Qt, QPoint
from PyQt5.QtGui import QPolygon

poly = QPolygon([QPoint(-1, 0), QPoint(0, 1), QPoint(1, 0), QPoint(0, -1)])

p = QPoint(0, 0)

print(poly.containsPoint(p, Qt.OddEvenFill))
