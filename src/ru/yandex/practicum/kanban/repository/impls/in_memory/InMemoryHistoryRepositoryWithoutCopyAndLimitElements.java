package ru.yandex.practicum.kanban.repository.impls.in_memory;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;

import java.util.*;

public class InMemoryHistoryRepositoryWithoutCopyAndLimitElements implements HistoryRepository {

    private MyLinkedList<Task> viewedTasks;

    public InMemoryHistoryRepositoryWithoutCopyAndLimitElements() {
        this.viewedTasks = new MyLinkedList<>();
    }

    @Override
    public void add(Task task) {
        viewedTasks.add(task);
    }

    @Override
    public List<Task> list() {
        return viewedTasks.toList();
    }

    @Override
    public void delete(int id) {
        viewedTasks.removeNodeIfPresent(id);
    }

    private static class MyLinkedList<E extends Task> {

        private Node<E> head;
        private Node<E> tail;
        private Map<Integer, Node<E>> map;

        public MyLinkedList() {
            this.map = new HashMap<>();
        }

        private static class Node<E> {
            E item;
            Node<E> next;
            Node<E> prev;

            Node(Node<E> prev, E element, Node<E> next) {
                this.item = element;
                this.next = next;
                this.prev = prev;
            }
        }

        void add(E e) {
            removeNodeIfPresent(e.getId());
            Node<E> node = linkToList(e);
            map.put(e.getId(), node);
        }

        void removeNodeIfPresent(int index) {
            Node<E> removingNode = map.get(index);
            if (Objects.nonNull(removingNode)) {
                if (removingNode == tail) {
                    removeTail();
                } else if (removingNode == head) {
                    removeHead();
                } else {
                    Node<E> next = removingNode.next;
                    Node<E> prev = removingNode.prev;
                    next.prev = prev;
                    prev.next = next;
                }
                map.remove(index);
            }
        }

        private void removeHead() {
            if (tail == null) {
                head = null;
            } else if (head.next == tail) {
                tail.prev = null;
                head = tail;
                tail = null;
            } else {
                Node<E> next = head.next;
                next.prev = null;
                head = next;
            }
        }

        private void removeTail() {
            if (tail.prev == head) {
                head.next = null;
                tail = null;
            } else {
                Node<E> prev = tail.prev;
                prev.next = null;
                tail = prev;
            }
        }

        Node<E> linkToList(E e) {
            Node<E> newNode = new Node<>(null, e, null);
            if (head == null) {
                head = newNode;
            } else if (tail == null) {
                head.next = newNode;
                newNode.prev = head;
                tail = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            }
            return newNode;
        }

        List<E> toList() {
            List<E> result = new ArrayList<>(map.size());
            for (Node<E> x = head; x != null; x = x.next) {
                result.add(x.item);
            }
            return result;
        }
    }
}