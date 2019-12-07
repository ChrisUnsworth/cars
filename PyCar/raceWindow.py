#!/usr/bin/env python3
"""UI for showing a race

Usage:

    python3 raceWindow.py
"""
from PyQt5 import QtGui
from PyQt5.QtWidgets import QMainWindow, QApplication
from PyQt5.QtGui import QPainter, QPen, QPolygon, QBrush
from PyQt5.QtCore import Qt, QTimer

from common.point import Point
from common.track import Track


class RaceWindow(QMainWindow):
    def __init__(self, track: Track, cars, text=''):
        super().__init__()

        self.track = track
        self.cars = cars
        self.title = 'Race: ' + text
        self.top = 100
        self.left = 100
        rect = track.outer_boundary.boundingRect()
        self.width = rect.width() + rect.x() * 2
        self.height = rect.height() + rect.y() * 2

        self.timer = QTimer()
        self.tick = 0
        self.init_window()
        self.timer.timeout.connect(self.on_tick)
        self.timer.start(100)

    def init_window(self):
        self.setWindowIcon(QtGui.QIcon("icon.png"))
        self.setWindowTitle(self.title)
        self.setGeometry(self.top, self.left, self.width, self.height)
        self.show()

    def on_tick(self):
        self.timer.stop()
        self.tick += 1
        self.repaint()
        self.timer.start(100)

    def paintEvent(self, e):
        self.paint_track()
        self.paint_start_arrow()
        self.paint_cars()
        self.paint_text()

    def paint_text(self):
        painter = QPainter(self)
        painter.setPen(QPen(Qt.black, 1, Qt.SolidLine))
        painter.drawText(10, 15, 'Tick:')
        painter.drawText(10, 30, str(self.tick))

    def paint_cars(self):
        painter = QPainter(self)
        painter.setPen(QPen(Qt.black, 1, Qt.SolidLine))
        painter.setBrush(QBrush(Qt.blue))
        for car in self.cars:
            painter.drawPolygon(car[self.tick % len(car)].position())

    def paint_track(self):
        painter = QPainter(self)
        painter.setPen(QPen(Qt.black, 1, Qt.SolidLine))
        painter.drawPolygon(self.track.outer_boundary)
        painter.drawPolygon(self.track.inner_boundary)

    def paint_start_arrow(self):
        painter = QPainter(self)
        painter.setPen(QPen(Qt.lightGray, 1, Qt.SolidLine))
        arrow = QPolygon(map(lambda p: p.rotate(self.track.start_angle) + self.track.start_point,
                             [Point(-16, 0), Point(-12, -12), Point(-8, 0),
                              Point(-4, 0), Point(0, -16), Point(4, 0),
                              Point(8, 0), Point(12, -12), Point(16, 0)]))
        painter.drawPolygon(arrow)

    @staticmethod
    def new(track, cars, text):
        app = QApplication([])
        window = RaceWindow(track, cars, text)
        app.exec_()
