-- phpMyAdmin SQL Dump
-- version 4.8.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Giu 05, 2019 alle 13:36
-- Versione del server: 10.1.34-MariaDB
-- Versione PHP: 7.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `catering`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `cookshift`
--

CREATE TABLE `cookshift` (
  `id` int(11) NOT NULL,
  `cook` int(11) NOT NULL,
  `shift` int(11) NOT NULL,
  `date` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=ascii;

--
-- Dump dei dati per la tabella `cookshift`
--

INSERT INTO `cookshift` (`id`, `cook`, `shift`, `date`) VALUES
(1, 5, 1, '03/05/2019');

-- --------------------------------------------------------

--
-- Struttura della tabella `events`
--

CREATE TABLE `events` (
  `id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `menu` int(11) DEFAULT NULL,
  `chef` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `events`
--

INSERT INTO `events` (`id`, `title`, `menu`, `chef`) VALUES
(1, 'matrimonio', 1, 3),
(2, 'comunione', 2, 3),
(3, 'evento 3', 2, 1),
(5, 'cerimonia', 2, 3);

-- --------------------------------------------------------

--
-- Struttura della tabella `job`
--

CREATE TABLE `job` (
  `id` int(11) NOT NULL,
  `recipe` int(11) NOT NULL,
  `cook` int(11) DEFAULT NULL,
  `shift` int(11) DEFAULT NULL,
  `eval` int(11) DEFAULT NULL,
  `portions` int(11) DEFAULT NULL,
  `completed` bit(1) DEFAULT b'0',
  `position` int(11) NOT NULL,
  `summarysheet` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=ascii;

--
-- Dump dei dati per la tabella `job`
--

INSERT INTO `job` (`id`, `recipe`, `cook`, `shift`, `eval`, `portions`, `completed`, `position`, `summarysheet`) VALUES
(1, 1, 1, 1, 1, 1, b'1', 3, 1),
(2, 4, 1, NULL, 5, 0, b'0', 0, 1),
(3, 12, NULL, NULL, 0, 0, b'0', 1, 1),
(4, 13, NULL, NULL, 0, 0, b'0', 2, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `menuitems`
--

CREATE TABLE `menuitems` (
  `id` int(11) NOT NULL,
  `menu` int(11) DEFAULT NULL,
  `section` int(11) DEFAULT '0',
  `description` tinytext,
  `recipe` int(11) NOT NULL,
  `position` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `menuitems`
--

INSERT INTO `menuitems` (`id`, `menu`, `section`, `description`, `recipe`, `position`) VALUES
(1, 3, 1, 'Voce 1', 1, 0),
(2, 3, 1, 'Voce 2', 1, 1),
(3, 3, 2, 'Voce 3', 1, 0),
(4, 3, 2, 'Voce 4', 1, 2),
(5, 3, 0, 'Voce 0', 1, 0),
(6, 3, 1, 'Voce 1.5', 1, 2),
(7, 3, 2, 'Voce 3.5', 1, 1),
(8, 3, 0, 'Voce 0.2', 1, 1),
(9, 3, 0, 'Voce 0.8', 1, 2),
(10, 3, 3, 'Voce 5', 1, 0),
(11, 3, 3, 'Voce 6', 1, 1),
(12, 3, 3, 'Voce 7', 1, 2);

-- --------------------------------------------------------

--
-- Struttura della tabella `menus`
--

CREATE TABLE `menus` (
  `id` int(11) NOT NULL,
  `title` tinytext NOT NULL,
  `menuowner` int(11) NOT NULL,
  `published` tinyint(1) DEFAULT NULL,
  `fingerFood` tinyint(1) DEFAULT NULL,
  `cookRequired` tinyint(1) DEFAULT NULL,
  `hotDishes` tinyint(1) DEFAULT NULL,
  `kitchenRequired` tinyint(1) DEFAULT NULL,
  `buffet` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `menus`
--

INSERT INTO `menus` (`id`, `title`, `menuowner`, `published`, `fingerFood`, `cookRequired`, `hotDishes`, `kitchenRequired`, `buffet`) VALUES
(1, 'prova in uso', 3, 1, 1, 0, 0, 0, 1),
(2, 'prova non in uso', 2, 0, 0, 1, 1, 1, 0),
(3, 'prova struttura', 3, 0, 0, 0, 0, 0, 1),
(4, 'Ciao', 3, 0, 0, 0, 0, 0, 0),
(7, 'prova struttura', 3, 0, 0, 0, 0, 0, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `recipes`
--

CREATE TABLE `recipes` (
  `id` int(11) NOT NULL,
  `name` varchar(128) DEFAULT NULL,
  `type` varchar(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `recipes`
--

INSERT INTO `recipes` (`id`, `name`, `type`) VALUES
(1, 'Salsa Tonnata', 'p'),
(2, 'Vitello Tonnato', 'r'),
(3, 'Vitello Tonnato all\'Antica', 'r'),
(4, 'Brodo di Manzo Ristretto', 'p'),
(5, 'Risotto alla Milanese', 'r'),
(6, 'Pesto Ligure', 'p'),
(7, 'Trofie avvantaggiate al pesto', 'r'),
(8, 'Orata al forno con olive', 'r'),
(9, 'Insalata russa', 'r'),
(10, 'Bagnet vert', 'p'),
(11, 'Acciughe al verde', 'r'),
(12, 'Agnolotti del plin', 'p'),
(13, 'Agnolotti al sugo d\'arrosto', 'r'),
(14, 'Agnolotti burro e salvia', 'r'),
(15, 'Brasato al barolo', 'r'),
(16, 'Panna cotta', 'r'),
(17, 'Tarte tatin', 'r');

-- --------------------------------------------------------

--
-- Struttura della tabella `roles`
--

CREATE TABLE `roles` (
  `id` varchar(1) NOT NULL,
  `role` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `roles`
--

INSERT INTO `roles` (`id`, `role`) VALUES
('c', 'Cuoco'),
('h', 'Chef'),
('o', 'Organizzatore'),
('s', 'Servizio');

-- --------------------------------------------------------

--
-- Struttura della tabella `sections`
--

CREATE TABLE `sections` (
  `menu` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `name` tinytext,
  `position` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `sections`
--

INSERT INTO `sections` (`menu`, `id`, `name`, `position`) VALUES
(3, 1, 'Primi', NULL),
(3, 2, 'Secondi', NULL),
(3, 3, 'Dessert', NULL);

-- --------------------------------------------------------

--
-- Struttura della tabella `shift`
--

CREATE TABLE `shift` (
  `id` int(11) NOT NULL,
  `timeFrom` text NOT NULL,
  `timeTo` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=ascii;

--
-- Dump dei dati per la tabella `shift`
--

INSERT INTO `shift` (`id`, `timeFrom`, `timeTo`) VALUES
(1, '7.30', '12.30'),
(2, '14', '19');

-- --------------------------------------------------------

--
-- Struttura della tabella `summarysheet`
--

CREATE TABLE `summarysheet` (
  `id` int(11) NOT NULL,
  `owner` int(11) NOT NULL,
  `event` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=ascii;

--
-- Dump dei dati per la tabella `summarysheet`
--

INSERT INTO `summarysheet` (`id`, `owner`, `event`) VALUES
(1, 3, 1),
(2, 3, 2),
(3, 3, 3),
(5, 3, 5);

-- --------------------------------------------------------

--
-- Struttura della tabella `userroles`
--

CREATE TABLE `userroles` (
  `user` int(11) NOT NULL,
  `role` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `userroles`
--

INSERT INTO `userroles` (`user`, `role`) VALUES
(1, 'o'),
(2, 'h'),
(2, 'c'),
(3, 'h'),
(4, 'o'),
(4, 'h'),
(5, 'c');

-- --------------------------------------------------------

--
-- Struttura della tabella `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(128) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `users`
--

INSERT INTO `users` (`id`, `name`) VALUES
(1, 'Marco'),
(2, 'Tony'),
(3, 'Viola'),
(4, 'Anna'),
(5, 'Giovanni');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `cookshift`
--
ALTER TABLE `cookshift`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `job`
--
ALTER TABLE `job`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `menuitems`
--
ALTER TABLE `menuitems`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `menus`
--
ALTER TABLE `menus`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `recipes`
--
ALTER TABLE `recipes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `sections`
--
ALTER TABLE `sections`
  ADD PRIMARY KEY (`id`),
  ADD KEY `Sections_Menu_id_fk` (`menu`);

--
-- Indici per le tabelle `shift`
--
ALTER TABLE `shift`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `summarysheet`
--
ALTER TABLE `summarysheet`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`id`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `cookshift`
--
ALTER TABLE `cookshift`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT per la tabella `events`
--
ALTER TABLE `events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT per la tabella `job`
--
ALTER TABLE `job`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT per la tabella `menuitems`
--
ALTER TABLE `menuitems`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT per la tabella `menus`
--
ALTER TABLE `menus`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT per la tabella `recipes`
--
ALTER TABLE `recipes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT per la tabella `sections`
--
ALTER TABLE `sections`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT per la tabella `shift`
--
ALTER TABLE `shift`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `summarysheet`
--
ALTER TABLE `summarysheet`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT per la tabella `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
