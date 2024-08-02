-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1:3306
-- Время создания: Авг 15 2023 г., 15:54
-- Версия сервера: 10.5.11-MariaDB
-- Версия PHP: 7.1.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `tanks`
--

-- --------------------------------------------------------

--
-- Структура таблицы `black_ips`
--

CREATE TABLE `black_ips` (
  `idblack_ips` bigint(20) NOT NULL,
  `ip` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Структура таблицы `daily_bonus_info`
--

CREATE TABLE `daily_bonus_info` (
  `uid` bigint(20) NOT NULL,
  `last_issue_bonuses` datetime NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Структура таблицы `garages`
--

CREATE TABLE `garages` (
  `uid` bigint(20) NOT NULL,
  `colormaps` longtext NOT NULL,
  `hulls` longtext NOT NULL,
  `inventory` longtext NOT NULL,
  `turrets` longtext NOT NULL,
  `effects` longtext NOT NULL,
  `userid` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Структура таблицы `karma`
--

CREATE TABLE `karma` (
  `idkarma` bigint(20) NOT NULL,
  `chat_banned` bit(1) DEFAULT NULL,
  `chat_banned_before` datetime DEFAULT NULL,
  `game_banned` bit(1) DEFAULT NULL,
  `game_banned_before` datetime DEFAULT NULL,
  `reason_for_chat_ban` varchar(255) DEFAULT NULL,
  `reason_for_game_ban` varchar(255) DEFAULT NULL,
  `userid` varchar(255) NOT NULL,
  `banner_chat_user_id` varchar(255) DEFAULT NULL,
  `banner_game_user_id` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Структура таблицы `logs`
--

CREATE TABLE `logs` (
  `id` bigint(20) NOT NULL,
  `date` datetime NOT NULL,
  `message` longtext NOT NULL,
  `type` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Структура таблицы `payment`
--

CREATE TABLE `payment` (
  `id` bigint(20) NOT NULL,
  `date` datetime NOT NULL,
  `id_payment` bigint(20) NOT NULL,
  `nickname` varchar(255) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `summ` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Структура таблицы `users`
--

CREATE TABLE `users` (
  `uid` bigint(20) NOT NULL,
  `crystalls` bigint(11) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_ip` varchar(255) NOT NULL,
  `last_issue_bonus` datetime DEFAULT NULL,
  `next_score` bigint(11) NOT NULL,
  `nickname` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `place` bigint(11) NOT NULL,
  `rank` bigint(11) NOT NULL,
  `rating` bigint(11) NOT NULL,
  `score` bigint(11) NOT NULL,
  `user_type` bigint(11) NOT NULL,
  `deaths` int(11) DEFAULT NULL,
  `kills` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `black_ips`
--
ALTER TABLE `black_ips`
  ADD PRIMARY KEY (`idblack_ips`),
  ADD UNIQUE KEY `idblack_ips` (`idblack_ips`);

--
-- Индексы таблицы `daily_bonus_info`
--
ALTER TABLE `daily_bonus_info`
  ADD PRIMARY KEY (`uid`),
  ADD UNIQUE KEY `uid` (`uid`);

--
-- Индексы таблицы `garages`
--
ALTER TABLE `garages`
  ADD PRIMARY KEY (`uid`),
  ADD UNIQUE KEY `uid` (`uid`),
  ADD UNIQUE KEY `userid` (`userid`);

--
-- Индексы таблицы `karma`
--
ALTER TABLE `karma`
  ADD PRIMARY KEY (`idkarma`);

--
-- Индексы таблицы `logs`
--
ALTER TABLE `logs`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`id`),
  ADD UNIQUE KEY `date` (`date`),
  ADD UNIQUE KEY `id_payment` (`id_payment`),
  ADD UNIQUE KEY `nickname` (`nickname`),
  ADD UNIQUE KEY `status` (`status`),
  ADD UNIQUE KEY `summ` (`summ`);

--
-- Индексы таблицы `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`uid`),
  ADD UNIQUE KEY `uid` (`uid`),
  ADD UNIQUE KEY `nickname` (`nickname`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `uid_2` (`uid`),
  ADD KEY `crystalls_2` (`crystalls`),
  ADD KEY `password` (`password`) USING BTREE,
  ADD KEY `next_score` (`next_score`) USING BTREE,
  ADD KEY `score` (`score`) USING BTREE,
  ADD KEY `crystalls` (`crystalls`) USING BTREE,
  ADD KEY `user_type` (`user_type`) USING BTREE,
  ADD KEY `place` (`place`) USING BTREE,
  ADD KEY `rating` (`rating`) USING BTREE,
  ADD KEY `rank` (`rank`) USING BTREE;

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `black_ips`
--
ALTER TABLE `black_ips`
  MODIFY `idblack_ips` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT для таблицы `daily_bonus_info`
--
ALTER TABLE `daily_bonus_info`
  MODIFY `uid` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблицы `garages`
--
ALTER TABLE `garages`
  MODIFY `uid` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=58;

--
-- AUTO_INCREMENT для таблицы `karma`
--
ALTER TABLE `karma`
  MODIFY `idkarma` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=56;

--
-- AUTO_INCREMENT для таблицы `logs`
--
ALTER TABLE `logs`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=415;

--
-- AUTO_INCREMENT для таблицы `payment`
--
ALTER TABLE `payment`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблицы `users`
--
ALTER TABLE `users`
  MODIFY `uid` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
